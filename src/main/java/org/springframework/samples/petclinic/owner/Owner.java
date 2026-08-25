package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/*
 * ◯オーナーエンティティ
 * @Entity：DBに保存（永続化）する対象クラスの目印。Javaが裏側でSQL文を自動生成しテーブル格納（JPAには必須）
 * @Table(name="owners")：紐づけるテーブル名を指定
 * @Column：DBのカラムとして登録
 * @Pattern：パターン指定
 * 	- regexp = "\\d{10}"：Regular Expression（レグエクスプ、正規表現）
 * 		\\d：0から9までの、いずれかの数字
 * 		{10}：10回連続
 * 		例）郵便番号："^\\d{3}-\\d{4}$"、半角の英数字：^[a-zA-Z0-9]+$"
 * - message：入力エラーが起きたときに画面に出す警告メッセージ、 { ... }の書き方でmessage.propertiesから参照
 */
@Entity
@Table(name="owners")
public class Owner {

	@Column
	@NotBlank
	private String address;
	
	@Column
	@NotBlank
	private String city;
	
	@Column
	@NotBlank
	@Pattern(regexp = "\\d{10}", message ="{telephone.invalid}")
	private String telephone;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	@OrderBy("name")
	private final List<Pet> pets = new ArrayList<>();
	
	public String getAddress() {
		return this.address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<Pet> getPets() {
		return this.pets;
	}

	
	
	
	
	
	
}
