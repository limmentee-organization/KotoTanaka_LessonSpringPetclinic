package org.springframework.samples.petclinic.owner;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

/*
 * ◯PetTypeのフォーマッター定義
 * ・@Component：このクラスを Spring が自動的に検出し、管理対象（Spring Bean）にする　※Formatter、Validator、ヘルパー類などに使用
 * →Spring Boot が起動時に自動的にこの Formatter を検知し、Webの型変換レジストリ（FormattingConversionService）へ登録
 */
@Component
public class PetTypeFormatter implements Formatter<PetType> {
	
	private final PetTypeRepository types;
	
	public PetTypeFormatter(PetTypeRepository types) {
		this.types = types;
	}
	
	/*
	 * ◯オブジェクト→文字列への変換
	 * ・フォーマッターがないとInteger.parseInt("cat") を実行し、NumberFormatException
	 */
	@Override
	public String print(PetType petType, Locale locale) {
		String name = petType.getName();
		return name != null ? name : "<null>";
	}
	
	/*
	 * ◯文字列→オブジェクトへの変換
	 */
	@Override
	public PetType parse(String text, Locale locale) throws ParseException {
		Collection<PetType> findPetTypes = this.types.findPetTypes();
		for (PetType type : findPetTypes) {
			if (Objects.equals(type.getName(), text)) {
				return type;
			}
		}
		throw new ParseException("type not found: " + text, 0);
	}
}
