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

import org.springframework.samples.petclinic.model.Person;

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
 * @OneToMany：「1 対 多」のリレーション設定　※Owner 1に対し Pet N
 * - cascade = CascadeType.ALL：親（Owner）の操作を紐づく子（Pet）に波及（カスケード）させる設定　※デフォルトは設定なし
 * - fetch = FetchType.EAGER：親を取得する際に子も即時DB読み込み　※デフォルトは FetchType.LAZY（遅延読み込み）
 * @JoinColumn(name = "owner_id")：子テーブル（pets）側の外部キーを指定。owner_idを使ってOwnerとPetの紐付けを指示
 * @OrderBy("name")：ORDER BY name ASCと同じ　※降順の場合は @OrderBy("name DESC")
 */
@Entity
@Table(name="owners")
public class Owner extends Person {

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

	public void addPet(Pet pet) {
		if (pet.isNew()) {
			// List<Pet>を取得し、新たなPetをリスト追加
			getPets().add(pet);
		}
	}
	
	/*
	 * ◯PetIdに基づき対象のペットを返す
	 * ・対象のペットIdがない場合はnullを返す
	 */
	public Pet getPet(Integer id) {
		for (Pet pet : getPets()) {
			if (!pet.isNew()) {
				Integer compId = pet.getId();		// compId ： 比較のために取り出したID（Comparing ID）
				if (compId == id) {
					return pet;
				}
			}
		}
		return null;
	}
}
