package kr.storekiosksystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProcessManager {
	// 멤버변수
	private static ProcessManager processManager = new ProcessManager();

	// 생성자
	public ProcessManager() {
		super();
	}

	// 멤버 함수
	public static ProcessManager getProcessManager() {
		return processManager;
	}

	// 화면을 clear하는 함수
	public void clear() {
		try {
			String operatingSystem = System.getProperty("os.name");
			if (operatingSystem.contains("Windows")) {
				ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
				Process startProcess = pb.inheritIO().start();
				startProcess.waitFor();
			} else {
				ProcessBuilder pb = new ProcessBuilder("clear");
				Process startProcess = pb.inheritIO().start();
				startProcess.waitFor();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println();
	}

	// 로그인 기능 함수
	public User loginMethod(Owner owner) {
		Scanner scan = new Scanner(System.in);
		// 아이디와 비밀번호 입력 받기
		System.out.print("아이디를 입력해주세요: ");
		String loginId = scan.nextLine();
		System.out.print("비밀번호를 입력해주세요: ");
		String loginPassword = scan.nextLine();
		boolean findFlag = false;
		User currentUser = null;
		// 로그인 처리
		for (User loginUser : owner.getUserList()) {
			if (loginUser.getUserId().equals(loginId) && loginUser.getUserPassword().equals(loginPassword)) {
				System.out.println("로그인 성공! 환영합니다, " + loginUser.getUserName() + "님.");
				currentUser = loginUser; // 로그인된 사용자 설정
				findFlag = true;
				StoreKioskSystemMain.loginFlag = true; // 로그인 성공 시 메인메뉴 출력
				StoreKioskSystemMain.loginSuccess = true; // 로그인 성공 상태 업데이트
				break;
			}
		}
		// 로그인 실패
		if (!findFlag) {
			System.out.println("로그인 실패! 다시입력해주세요.");
			StoreKioskSystemMain.loginSuccess = false; // 로그인 성공 상태 업데이트
			return null; // 로그인 실패 시 false 반환
		} else {
			return currentUser;
		}
	}

	// 회원가입 기능 함수
	public void signUpMethod(Owner owner) {
		String userName = inputName();
		String userId = null;
		boolean serchFlag = false;
		// 사용자아이디 패턴검색함수
		while (!serchFlag) {
			userId = inputId();
			boolean isDuplicate = false; // 중복 여부 체크 변수
			for (User serchUser : owner.getUserList()) {
				if (serchUser.getUserId().equals(userId)) {
					System.out.println("아이디가 중복되었습니다. 다시 입력해주세요");
					isDuplicate = true; // 중복되면 플래그를 true로 설정
					break;
				}
			}
			// 중복되지 않으면 serchFlag를 true로 설정하여 반복문 종료
			if (!isDuplicate) {
				serchFlag = true;
			}
		}
		String userPassword = inputPassWord();
		String userPhoneNum = inputPhoneNumber();
		User user = new User(userId, userPassword, userName, userPhoneNum);
		owner.addUserList(user);
		userSave(owner.getUserList());
		System.out.printf("%s님 회원가입이 완료되었습니다.\n", userName);
	}

	// 메뉴선택 후 장바구니 담는 기능 함수
	public void choiceMenu(Owner owner, User currentUser) {
		Scanner scan = new Scanner(System.in);
		int page = 1;
		while (true) {
			// 전체페이지를 구한다.
			clear();
			int totalPage = owner.getStoreMenu().size() / 5;
			int remainValue = owner.getStoreMenu().size() % 5; // 나머지 값
			if (remainValue != 0) {
				totalPage += 1;
			}
			// 해당되는 페이지 시작위치, 끝위치
			int start = 5 * (page - 1);
			int stop = 5 * (page - 1) + 5;

			// 마지막 페이지일때 나머지값이 있을때 끝위치 1~4증가
			if (page == totalPage && remainValue != 0) {
				stop = 5 * (page - 1) + remainValue;
			}
			System.out.printf("전체 %dpage/ 현재 %dpage \n", totalPage, page);
			System.out.printf(
					"――――――――――――――――――――――――――――――――――――――――――%dPage――――――――――――――――――――――――――――――――――――――――――\n",
					page);
			for (int i = start; i < stop; i++) {
				System.out.println((i + 1) + "번 " + owner.getStoreMenu().get(i).toString()); // 음식점 메뉴 번호 붙여서 출력
			}
			System.out.println(
					"―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
			do {
				System.out.printf("(-1 : exit) 페이지선택> ");
				page = Integer.parseInt(scan.nextLine());
				boolean isPageCheck = Pattern.matches("^[-1-9]{1,3}$", String.valueOf(page));
				if (isPageCheck == true && -1 <= page && page <= totalPage) {
					break;
				} else {
					System.out.printf("페이지 범위를 넘었습니다. 다시 입력해주세요(1-%d)\n", totalPage);
				}
			} while (true);
			if (page == -1) {
				break;
			}
		}
		int no = 0;
		do {
			System.out.print("장바구니에 담으실 번호를 입력해주세요: "); // 사용자가 번호를 입력해 메뉴 선택
			String data = scan.nextLine();
			boolean isInputCheck = true;
			try {
				no = Integer.parseInt(data); // 선택한 메뉴번호를 저장
			} catch (Exception e) {
				isInputCheck = false;
			}
			if (isInputCheck == true && (1 <= no && no <= owner.getStoreMenu().size())) { // 메뉴 사이즈보다 숫자가 넘어가면 다시요청
				break;
			}
			System.out.printf("번호선택오류발생 다시입력요청(1~%d)\n", owner.getStoreMenu().size());
		} while (true);
		Menu menu = owner.getStoreMenu().get(no - 1); // 인덱스가 0부터 시작이니까 1을 빼주고 선택한 메뉴를 메뉴 객체 생성
		currentUser.getUserCart().addCartMenu(menu); // 현재 사용자 장바구니에 메뉴 추가
		currentUser.getUserCart().addTotalAmount(menu.getPrice()); // 현재 사용자 장바구니에 메뉴 가격을 총액에 더함
	}

	// 결제 기능 함수
	public void amountMethod(Owner owner, User currentUser) {
		int totalAmount = currentUser.getUserCart().getTotalAmount();
		System.out.println("――――――――――" + owner.getStoreName() + "―――――――――――");
		System.out.print("1.매장 ");
		System.out.println("2.포장 ");
		System.out.println("―――――――――――――――――――――――――");
		int paymentNo = paymentSelectNo();
		char yesNo = 0;
		switch (paymentNo) {
		case 1: // 매장 주문결제
			cartAccountPrint(currentUser);
			yesNo = inputYesNo();
			if (yesNo == 'y' || yesNo == 'Y') {
				System.out.println("결제가 완료되었습니다.");
				owner.setDailySales(totalAmount);
				currentUser.getUserCart().cartClear();
			} else {
				System.out.println("결제가 취소되었습니다.");
			}
			break;
		case 2: // 포장 주문결제
			cartAccountPrint(currentUser);
			yesNo = inputYesNo();
			if (yesNo == 'y' || yesNo == 'Y') {
				System.out.println("결제가 완료되었습니다.");
				owner.setDailySales(totalAmount);
				currentUser.getUserCart().cartClear();
			} else {
				System.out.println("결제가 취소되었습니다.");
			}
			break;
		}
	}

	// 사용자 계산서 출력
	public void cartAccountPrint(User currentUser) {
		int totalAmount = currentUser.getUserCart().getTotalAmount();
		System.out.println("――――――" + currentUser.getUserName() + "님 계산서――――――");
		for (int i = 0; i < currentUser.getUserCart().getCartMenu().size(); i++) {
			System.out.println(currentUser.getUserCart().getCartMenu().get(i).toString());
		}
		System.out.printf("총 금액은 %d원입니다.\n", totalAmount);
		System.out.println("―――――――――――――――――――――――――");
	}

	// 사용자 장바구니 출력
	public void cartPrint(User currentUser) {
		System.out.println("――――――" + currentUser.getUserName() + "님 장바구니――――――");
		for (int i = 0; i < currentUser.getUserCart().getCartMenu().size(); i++) {
			System.out.println(currentUser.getUserCart().getCartMenu().get(i).toString());
		}
		System.out.println("―――――――――――――――――――――――――");
	}

	// 관리자 메뉴 출력
	public void owerMenuDisPaly(Owner owner) {
		System.out.println("―――――――――――――관리자님――――――――――――");
		System.out.println("1.회원리스트확인 ");
		System.out.println("2.메뉴리스트확인 ");
		System.out.println("3.메뉴추가");
		System.out.println("4.메뉴삭제");
		System.out.println("5.메뉴수정");
		System.out.println("6.하루매출확인");
		System.out.println("7.사용자모드");
		System.out.println("―――――――――――――――――――――――――――――――");
	}

	// 메뉴 추가 함수
	public void menuAdd(Owner owner) {
		Scanner scan = new Scanner(System.in);
		System.out.print("추가할 메뉴명을 입력해주세요: ");
		String menuName = scan.nextLine();
		System.out.print("추가할 메뉴의 가격을 입력해주세요: ");
		int menuPrice = Integer.parseInt(scan.nextLine());
		Menu addMenu = new Menu(menuName, menuPrice);
		owner.addStoreMenu(addMenu);
	}

	// 사용자 메뉴 출력
	public void userMenuDisPlay(Owner owner) {
		System.out.println("――――――――――" + owner.getStoreName() + "―――――――――――");
		System.out.println("1.로그인 ");
		System.out.println("2.메뉴선택 ");
		System.out.println("3.장바구니확인");
		System.out.println("4.장바구니비우기");
		System.out.println("5.결제하기(매장&포장)");
		System.out.println("6.관리자모드");
		System.out.println("7.종료");
		System.out.println("―――――――――――――――――――――――――");
	}

	// 로그인 메뉴 출력
	public void loginMenuDisPlay(Owner owner) {
		System.out.println("――――――――――" + owner.getStoreName() + "―――――――――――");
		System.out.println("1.로그인 ");
		System.out.println("2.회원가입 ");
		System.out.println("―――――――――――――――――――――――――");
	}

	// 사용자정보리스트를 파일에 저장
	public void userSave(ArrayList<User> userList) {
		{
			// 파일에서 가져오고 보조스트림을 정의한다.(Scanner)
			FileOutputStream fo = null;
			PrintStream out = null;
			try {
				fo = new FileOutputStream("E:/javaWorkspace/storekiosksystem/res/userList.txt");
				out = new PrintStream(fo);
				// 파일 메뉴를 추가한다.
				out.printf("%s", StoreKioskSystemMain.userTitle);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ArrayList 내용을 한개씩 가져와서 파일에 저장한다.
			for (int i = 0; i < userList.size(); i++) {
				User user = userList.get(i);
				out.printf("\n%s,%s,%s,%s", user.getUserId(), user.getUserPassword(), user.getUserName(),
						user.getUserPhoneNum()); // 앞에 \n 필수!!
			}
			out.close();
			try {
				fo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	// 메뉴정보리스트를 파일에 저장
	public void menuSave(ArrayList<Menu> menuList) {
		{
			// 파일에서 가져오고 보조스트림을 정의한다.(Scanner)
			FileOutputStream fo = null;
			PrintStream out = null;
			try {
				fo = new FileOutputStream("E:/javaWorkspace/storekiosksystem/res/menuList.txt");
				out = new PrintStream(fo);
				// 파일 메뉴를 추가한다.
				out.printf("%s", StoreKioskSystemMain.menuTitle);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// ArrayList 내용을 한개씩 가져와서 파일에 저장한다.
			for (int i = 0; i < menuList.size(); i++) {
				Menu menu = menuList.get(i);
				out.printf("\n%s,%d", menu.getMenuName(), menu.getPrice()); // 앞에 \n 필수!!
			}
			out.close();
			try {
				fo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	// 사용자 정보 파일을 ArrayList에 업로드
	public void userFileUpLoad(ArrayList<User> userList) {
		// 파일에서 가져오고 보조스트림을 정의한다.(Scanner)
		FileInputStream fi;
		try {
			// fi = new FileInputStream("res/dog.txt");
			fi = new FileInputStream("E:/javaWorkspace/storekiosksystem/res/userList.txt");
			Scanner scan = new Scanner(fi);
			// 첫라인 없앤다.
			if (scan.hasNextLine()) {
				StoreKioskSystemMain.userTitle = scan.nextLine();
			}
			// 반복문을 통해서 모든 한라인씩을 가져와서 => String tokens
			// => 형변환시켜서 =>StudentData 객체 => ArrayList 입력
			while (true) {
				if (!scan.hasNextLine()) {
					break;
				}
				String data = scan.nextLine();
				String[] tokens = data.split(",");
				String dogName = tokens[0];
				String userId = tokens[0];
				String userPassword = tokens[1];
				String userName = tokens[2];
				String userPhoneNum = tokens[3];
				User user = new User(userId, userPassword, userName, userPhoneNum);
				userList.add(user);
			} // end of while
				// System.out.println("파일에서 ArrayList에 로드되었습니다.");
			fi.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 메뉴 정보 파일을 ArrayList에 업로드
	public void menuFileUpLoad(ArrayList<Menu> menuList) {
		// 파일에서 가져오고 보조스트림을 정의한다.(Scanner)
		FileInputStream fi;
		try {
			// fi = new FileInputStream("res/dog.txt");
			fi = new FileInputStream("E:/javaWorkspace/storekiosksystem/res/menuList.txt");
			Scanner scan = new Scanner(fi);
			// 첫라인 없앤다.
			if (scan.hasNextLine()) {
				StoreKioskSystemMain.menuTitle = scan.nextLine();
			}
			// 반복문을 통해서 모든 한라인씩을 가져와서 => String tokens
			// => 형변환시켜서 =>StudentData 객체 => ArrayList 입력
			while (true) {
				if (!scan.hasNextLine()) {
					break;
				}
				String data = scan.nextLine();
				String[] tokens = data.split(",");
				String menuName = tokens[0];
				int price = Integer.parseInt(tokens[1]);
				Menu menu = new Menu(menuName, price);
				menuList.add(menu);
			} // end of while
				// System.out.println("파일에서 ArrayList에 로드되었습니다.");
			fi.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 사용자메뉴선택함수
	public int userSelectNo() {
		Scanner scan = new Scanner(System.in);
		int no = 0;
		do {
			System.out.print("번호> ");
			String data = scan.nextLine();
			// boolean isInputCheck = Pattern.matches("^[1-7]{1}&", String.valueOf(no));
			boolean isInputCheck = true;
			try {
				no = Integer.parseInt(data);
			} catch (Exception e) {
				isInputCheck = false;
			}
			if (isInputCheck == true && (1 <= no && no <= 7)) {
				break;
			}
			System.out.println("번호선택오류발생 다시입력요청(1~7)");
		} while (true);
		return no;
	}

	// 관리자메뉴선택함수
	public int ownerSelectNo() {
		Scanner scan = new Scanner(System.in);
		int no = 0;
		do {
			System.out.print("번호> ");
			String data = scan.nextLine();
			// boolean isInputCheck = Pattern.matches("^[1-7]{1}&", String.valueOf(no));
			boolean isInputCheck = true;
			try {
				no = Integer.parseInt(data);
			} catch (Exception e) {
				isInputCheck = false;
			}
			if (isInputCheck == true && (1 <= no && no <= 7)) {
				break;
			}
			System.out.println("번호선택오류발생 다시입력요청(1~7)");
		} while (true);
		return no;
	}

	// 로그인&회원가입 메뉴선택함수
	public int loginSelectNo() {
		Scanner scan = new Scanner(System.in);
		int no = 0;
		do {
			System.out.print("번호> ");
			String data = scan.nextLine();
			// boolean isInputCheck = Pattern.matches("^[1-7]{1}&", String.valueOf(no));
			boolean isInputCheck = true;
			try {
				no = Integer.parseInt(data);
			} catch (Exception e) {
				isInputCheck = false;
			}
			if (isInputCheck == true && (1 <= no && no <= 3)) {
				break;
			}
			System.out.println("번호선택오류발생 다시입력요청(1~3)");
		} while (true);
		return no;
	}

	// 버거 선택 패턴검색 함수
	public int inputChoiceMemu() {
		Scanner scan = new Scanner(System.in);
		int no = 0;
		do {
			System.out.print("번호> ");
			String data = scan.nextLine();
			// boolean isInputCheck = Pattern.matches("^[1-7]{1}&", String.valueOf(no));
			boolean isInputCheck = true;
			try {
				no = Integer.parseInt(data);
			} catch (Exception e) {
				isInputCheck = false;
			}
			if (isInputCheck == true && (1 <= no && no <= 2)) {
				break;
			}
			System.out.println("번호선택오류발생 다시입력요청(1~2)");
		} while (true);
		return no;
	}

	// 결제방식 선택함수
	public int paymentSelectNo() {
		Scanner scan = new Scanner(System.in);
		int no = 0;
		do {
			System.out.print("번호> ");
			String data = scan.nextLine();
			// boolean isInputCheck = Pattern.matches("^[1-7]{1}&", String.valueOf(no));
			boolean isInputCheck = true;
			try {
				no = Integer.parseInt(data);
			} catch (Exception e) {
				isInputCheck = false;
			}
			if (isInputCheck == true && (1 <= no && no <= 2)) {
				break;
			}
			System.out.println("번호선택오류발생 다시입력요청(1~2)");
		} while (true);
		return no;
	}

	// 결제선택 패턴검색함수
	public char inputYesNo() {
		Scanner scan = new Scanner(System.in);
		char yesNo = 0;
		do {
			System.out.print("결제 하시겠습니까?(Y,N): ");
			yesNo = scan.nextLine().charAt(0);
			boolean isYesNoCheck = Pattern.matches("^[yYnN]$", String.valueOf(yesNo));
			if (isYesNoCheck == true) {
				break;
			} else {
				System.out.println("올바르게 입력해주세요.");
			}
		} while (true);
		return yesNo;
	}

	// 사용자 이름 입력 패턴검색함수
	public String inputName() {
		Scanner scan = new Scanner(System.in);
		String name = null;
		do {
			System.out.print("이름을 입력해주세요: ");
			name = scan.nextLine();
			boolean isNameCheck = Pattern.matches("^[가-힣]{2,5}$", name);
			if (isNameCheck == true) {
				break;
			} else {
				System.out.println("한글로 입력해주세요.(2~5자이내)");
			}
		} while (true);
		return name;
	}

	// 사용자 아이디 입력 패턴검색함수
	public String inputId() {
		Scanner scan = new Scanner(System.in);
		String id = null;
		do {
			System.out.print("아이디를 입력해주세요: ");
			id = scan.nextLine();
			boolean isNameCheck = Pattern.matches("^[a-z]{1}[a-z0-9]{4,10}$", id);
			if (isNameCheck == true) {
				break;
			} else {
				System.out.println("5~10자리로 입력해주세요!");
			}
		} while (true);
		return id;
	}

	// 사용자 패스워드 입력 패턴검색함수
	public String inputPassWord() {
		Scanner scan = new Scanner(System.in);
		String passWord = null;
		do {
			System.out.print("비밀번호를 입력해주세요: ");
			passWord = scan.nextLine();
			boolean isNameCheck = Pattern.matches("^(?=.*[0-9])(?=.*[a-z]).{6,}$", passWord);
			if (isNameCheck == true) {
				break;
			} else {
				System.out.println("6자리이상으로 영문, 숫자포함으로 입력해주세요!");
			}
		} while (true);
		return passWord;
	}

	// 사용자 전화번호 입력 패턴검색함수
	public String inputPhoneNumber() {
		Scanner scan = new Scanner(System.in);
		String PhoneNumber = null;
		do {
			System.out.print("전화번호를 입력해주세요: ");
			PhoneNumber = scan.nextLine();
			boolean isNameCheck = Pattern.matches("^[\\d]{11}$", PhoneNumber);
			if (isNameCheck == true) {
				break;
			} else {
				System.out.println("-없이 11자로 입력해주세요.");
			}
		} while (true);
		return PhoneNumber;
	}

	// 회원정보를 페이징 기법으로 출력
	public void userPrint(Owner owner) {
		Scanner scan = new Scanner(System.in);
		int page = 1;
		while (true) {
			// 전체페이지를 구한다.
			clear();
			int totalPage = owner.getUserList().size() / 5;
			int remainValue = owner.getUserList().size() % 5; // 나머지 값
			if (remainValue != 0) {
				totalPage += 1;
			}
			// 해당되는 페이지 시작위치, 끝위치
			int start = 5 * (page - 1);
			int stop = 5 * (page - 1) + 5;

			// 마지막 페이지일때 나머지값이 있을때 끝위치 1~4증가
			if (page == totalPage && remainValue != 0) {
				stop = 5 * (page - 1) + remainValue;
			}
			System.out.printf("전체 %dpage/ 현재 %dpage \n", totalPage, page);
			System.out.printf(
					"――――――――――――――――――――――――――――――――――――――――――%dPage――――――――――――――――――――――――――――――――――――――――――\n",
					page);
			for (int i = start; i < stop; i++) {
				System.out.println(owner.getUserList().get(i).toString());
			}
			System.out.println(
					"―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
			do {
				System.out.printf("(-1 : exit) 페이지선택> ");
				page = Integer.parseInt(scan.nextLine());
				boolean isPageCheck = Pattern.matches("^[-1-9]{1,3}$", String.valueOf(page));
				if (isPageCheck == true && -1 <= page && page <= totalPage) {
					break;
				} else {
					System.out.printf("페이지 범위를 넘었습니다. 다시 입력해주세요(1-%d)\n", totalPage);
				}
			} while (true);
			if (page == -1) {
				break;
			}
		}
	}

	// 메뉴정보를 페이징 기법으로 출력
	public void menuPrint(Owner owner) {
		Scanner scan = new Scanner(System.in);
		int page = 1;
		while (true) {
			// 전체페이지를 구한다.
			clear();
			int totalPage = owner.getStoreMenu().size() / 5;
			int remainValue = owner.getStoreMenu().size() % 5; // 나머지 값
			if (remainValue != 0) {
				totalPage += 1;
			}
			// 해당되는 페이지 시작위치, 끝위치
			int start = 5 * (page - 1);
			int stop = 5 * (page - 1) + 5;

			// 마지막 페이지일때 나머지값이 있을때 끝위치 1~4증가
			if (page == totalPage && remainValue != 0) {
				stop = 5 * (page - 1) + remainValue;
			}
			System.out.printf("전체 %dpage/ 현재 %dpage \n", totalPage, page);
			System.out.printf(
					"――――――――――――――――――――――――――――――――――――――――――%dPage――――――――――――――――――――――――――――――――――――――――――\n",
					page);
			for (int i = start; i < stop; i++) {
				System.out.println(owner.getStoreMenu().get(i).toString());
			}
			System.out.println(
					"―――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
			do {
				System.out.printf("(-1 : exit) 페이지선택> ");
				page = Integer.parseInt(scan.nextLine());
				boolean isPageCheck = Pattern.matches("^[-1-9]{1,3}$", String.valueOf(page));
				if (isPageCheck == true && -1 <= page && page <= totalPage) {
					break;
				} else {
					System.out.printf("페이지 범위를 넘었습니다. 다시 입력해주세요(1-%d)\n", totalPage);
				}
			} while (true);
			if (page == -1) {
				break;
			}
		}
	}

	// 메뉴이름으로 검색하여 메뉴정보를 삭제하는 함수
	public void menuDelete(ArrayList<Menu> menuList) {
		{
			Scanner scan = new Scanner(System.in);
			System.out.print("삭제할 메뉴이름: ");
			String name = scan.nextLine();
			boolean removeFlag = false;
			for (Menu menu : menuList) {
				if (menu.getMenuName().equals(name)) {
					menuList.remove(menu);
					removeFlag = true;
					System.out.printf("%s\n", menu.toString());
					System.out.printf("삭제할 %s라는 메뉴가 삭제되었습니다.\n", name);
					break;
				}
			}
			if (!removeFlag) {
				System.out.printf("삭제할 %s라는 메뉴가 존재하지 않습니다.\n", name);
			}
		}
	}

	// 메뉴이름으로 검색하여 메뉴가격을 수정하는 함수
	public void menuUpdate(ArrayList<Menu> menuList) {
		{
			Scanner scan = new Scanner(System.in);
			System.out.print("가격을 수정할 메뉴이름을 입력해주세요: ");
			String name = scan.nextLine();
			Menu findMenu = null;
			for (Menu menu : menuList) {
				if (menu.getMenuName().equals(name)) {
					findMenu = menu;
					break;
				}
			}
			if (findMenu == null) {
				System.out.printf("수정할 %s이라는 메뉴가 존재하지 않습니다.\n", name);
				return;
			}
			System.out.printf("현재메뉴의 가격은: %d => 수정할가격 > ", findMenu.getPrice());
			int menuPrice = Integer.parseInt(scan.nextLine());
			findMenu.setPrice(menuPrice);
			System.out.printf("%s 메뉴 수정 완료\n", name);
			return;
		}
	}
}
