package kr.storekiosksystem;

import java.util.Scanner;
import java.util.regex.Pattern;

public class StoreKioskSystemMain {
	public static String userTitle; // userList파일 첫줄 저장
	public static String menuTitle; // menuList파일 첫줄 저장
	public static boolean loginSuccess = false;
	public static boolean loginFlag = false;

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		ProcessManager pm = new ProcessManager();
		Owner owner = new Owner(); // 관리자 생성
		pm.userFileUpLoad(owner.getUserList()); // userList파일 ArrayList에 저장
		pm.menuFileUpLoad(owner.getStoreMenu()); // menuList파일 ArrayList에 저장
		// 변수선언
		boolean stopFlag = false;
		User currentUser = null; // 현재 로그인한 유저
		while (!stopFlag) { // 프로그램 실행
			// 메뉴출력
			pm.clear();
			pm.userMenuDisPlay(owner);
			// 메뉴선택실행
			int userNo = pm.userSelectNo();
			switch (userNo) {
			case 1: // 로그인 기능
				while (!loginFlag) {
					pm.clear();
					pm.loginMenuDisPlay(owner);
					int loginNo = pm.loginSelectNo();
					switch (loginNo) {
					case 1: // 로그인 기능
						// 로그인 기능 함수
						currentUser = pm.loginMethod(owner);
						// clear 기능 대기
						System.out.println("continue>>");
						scan.nextLine();
						break;
					case 2: // 회원가입 기능
						// 회원가입 기능 함수
						pm.signUpMethod(owner);
						// clear 기능 대기
						System.out.println("continue>>");
						scan.nextLine();
						break;
					}// end of case1
				} // end of while
				break;
			case 2: // 메뉴 선택 기능
				if (!loginSuccess) { // 로그인 여부 체크
					System.out.println("로그인 후 이용 가능합니다.");
					// clear 기능 대기
					System.out.println("continue>>");
					scan.nextLine();
					break; // 로그인되지 않았다면 기능 진행하지 않음
				}
				// 메뉴 선택 후 장바구니에 담는 기능 함수
				pm.choiceMenu(owner, currentUser);
				// clear 기능 대기
				System.out.println("continue>>");
				scan.nextLine();
				break;
			case 3: // 장바구니 확인 기능
				if (!loginSuccess) { // 로그인 여부 체크
					System.out.println("로그인 후 이용 가능합니다.");
					// clear 기능 대기
					System.out.println("continue>>");
					scan.nextLine();
					break; // 로그인되지 않았다면 기능 진행하지 않음
				}
				// 장바구니 출력 기능 함수
				pm.cartPrint(currentUser);
				// clear 기능 대기
				System.out.println("continue>>");
				scan.nextLine();
				break;
			case 4: // 장바구니 비우기 기능
				if (!loginSuccess) { // 로그인 여부 체크
					System.out.println("로그인 후 이용 가능합니다.");
					// clear 기능 대기
					System.out.println("continue>>");
					scan.nextLine();
					break; // 로그인되지 않았다면 기능 진행하지 않음
				}
				currentUser.getUserCart().cartClear();
				// clear 기능 대기
				System.out.println("continue>>");
				scan.nextLine();
				break;
			case 5: // 결제하기 기능
				if (!loginSuccess) { // 로그인 여부 체크
					System.out.println("로그인 후 이용 가능합니다.");
					// clear 기능 대기
					System.out.println("continue>>");
					scan.nextLine();
					break; // 로그인되지 않았다면 기능 진행하지 않음
				}
				// 결제 기능 함수 (매장, 포장 -> 결제하기(y,n))
				pm.amountMethod(owner, currentUser);
				// clear 기능 대기
				System.out.println("continue>>");
				scan.nextLine();
				break;
			case 6: // 관리자모드
				if (!loginSuccess) { // 로그인 여부 체크
					System.out.println("관리자 아이디만 접속가능합니다.");
					// clear 기능 대기
					System.out.println("continue>>");
					scan.nextLine();
					break; // 로그인되지 않았다면 기능 진행하지 않음
				} else if (currentUser.getUserId().equals("admin")) {
					boolean _stopFlag = false;
					while (!_stopFlag) {
						pm.clear();
						// 관리자 메뉴 출력함수
						pm.owerMenuDisPaly(owner);
						int ownerNo = pm.ownerSelectNo();
						switch (ownerNo) {
						case 1: { // 회원리스트를 페이징기법으로 확인
							pm.userPrint(owner);

							// clear 기능 대기
							System.out.println("continue>>");
							scan.nextLine();
							break;
						}
						case 2: { // 메뉴리스트를 페이징기법으로 확인
							pm.menuPrint(owner);
							// clear 기능 대기
							System.out.println("continue>>");
							scan.nextLine();
							break;
						}
						case 3: { // 메뉴추가
							pm.menuAdd(owner);
							pm.menuSave(owner.getStoreMenu());
							// clear 기능 대기
							System.out.println("continue>>");
							scan.nextLine();
							break;
						}
						case 4: { // 메뉴삭제
							pm.menuPrint(owner);
							pm.menuDelete(owner.getStoreMenu());
							pm.menuSave(owner.getStoreMenu());
							// clear 기능 대기
							System.out.println("continue>>");
							scan.nextLine();
							break;
						}
						case 5: { // 메뉴수정
							pm.menuPrint(owner);
							pm.menuUpdate(owner.getStoreMenu());
							pm.menuSave(owner.getStoreMenu());
							// clear 기능 대기
							System.out.println("continue>>");
							scan.nextLine();
							break;
						}
						case 6: { // 하루매출확인
							System.out.printf("하루 매출금액은 %d원입니다!\n", owner.getDailySales());
							// clear 기능 대기
							System.out.println("continue>>");
							scan.nextLine();
							break;
						}
						case 7: // 사용자모드전환
							_stopFlag = true;
							break;

						}// end of switch
					}
					break;
				}else {
					System.out.println("관리자 아이디만 접속가능합니다.");
					// clear 기능 대기
					System.out.println("continue>>");
					scan.nextLine();
					break; // 로그인되지 않았다면 기능 진행하지 않음
				}
			case 7:// 프로그램종료
				stopFlag = true;
				break;
			}// end of switch
		} // end of while
		scan.close();
	} // end of main

}
