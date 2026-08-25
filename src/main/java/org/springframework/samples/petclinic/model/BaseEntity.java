package org.springframework.samples.petclinic.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/*
 * ◯ベースエンティティ
 * @MappedSuperclass：DBの共通項目をまとめるための親クラスを示す
 */
@MappedSuperclass
public class BaseEntity {

	@Id		// DBのPKに設定
	@GeneratedValue(strategy = GenerationType.IDENTITY)		// 自動採番、AUTO_INCREMENTと同じ
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public boolean isNew() {		// 新人かどうかを確かめるメソッド
		return this.id == null;		// 新人（idがない）ならturuを返す 
	}
	
}
