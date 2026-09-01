package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;

/*
 * ◯ベースエンティティ
 * @MappedSuperclass：DBの共通項目をまとめるための親クラスを示す
 */
@MappedSuperclass
public class NamedEntity extends BaseEntity {

	@Column
	@NotBlank
	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		String name = this.getName();
		return name != null ? name : "<null>";		// 三項演算子：name != null Yesならname, Noなら<null>を返す
	}
	
}
