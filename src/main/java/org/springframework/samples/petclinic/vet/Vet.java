package org.springframework.samples.petclinic.vet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlElement;

import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.model.Person;

@Entity		// JPAが提供するアノテーション。DBと紐づくオブジェクト（エンティティ）であることを明示
@Table(name = "vets")		// JPAが提供するアノテーション。どのテーブルと紐づけるかを指定
public class Vet extends Person {

	/*
	 * ・@ManyToMany：JPAが提供するアノテーション。多対多を表す。
	 * 　- FetchType.EAGER：DBからデータ取得するタイミングを「即時取得」とする指定
	 * 　- FetchType.LAZY：「遅延取得」（必要になったタイミングでDBからデータ取得）※通常こちらがデフォルト
	 * ・@JoinTable：JPAが提供するアノテーション。多対多を結ぶ中間テーブルをJPAに認識させるためのアノテーション
	 * 　- name = "vet_specialties"：中間テーブルのテーブル名を指定
	 * 　- joinColumns = @JoinColumn(name = "vet_id")：中間テーブルからこのクラス（Vet）を紐づけるためのカラム名を指定
	 * 　- inverseJoinColumns = @JoinColumn(name = "specialty_id")：中間テーブルから相手側（Specialty）を紐づけるためのカラム名を指定
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"),	
	inverseJoinColumns = @JoinColumn(name = "specialty_id"))
	private Set<Specialty> specialties;
	
	protected Set<Specialty> getSpecialtiesInternal(){
		if (this.specialties == null) {
			this.specialties = new HashSet<>();
		}
		return this.specialties;
	}
	
	@XmlElement
	public List<Specialty> getSpecialties(){
		return getSpecialtiesInternal().stream()
				.sorted(Comparator.comparing(NamedEntity::getName))
				.collect(Collectors.toList());
	}
	
	public int getNrOfSpecialties() {
		return getSpecialtiesInternal().size();
	}
	
	public void addSpecialty(Specialty specialty) {
		getSpecialtiesInternal().add(specialty);
	}
}
