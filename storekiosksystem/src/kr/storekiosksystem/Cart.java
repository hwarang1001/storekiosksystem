package kr.storekiosksystem;

import java.util.ArrayList;

public class Cart {
	// 변수
	private String userName;
	private ArrayList<Menu> cartMenu; // 장바구니 음식점 메뉴 관리
	private int totalAmount;

	// 생성자
	public Cart() {
		this(null);
	}
	public Cart(String userName) {
		super();
		this.userName = userName;
		this.cartMenu = new ArrayList<>();
	}

	public void cartClear() {
		this.cartMenu.clear();
		this.totalAmount = 0;
		System.out.println("장바구니를 비웠습니다.");
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ArrayList<Menu> getCartMenu() {
		return cartMenu;
	}

	public void addCartMenu(Menu menu) {
		this.cartMenu.add(menu);
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void addTotalAmount(int Price) {
		this.totalAmount += Price;
	}
	
}
