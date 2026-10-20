package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VetController {

	public final VetRepository vetRepository;
	public VetController(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}
	
	@GetMapping("vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		Page<Vet> paginated = findPaginated(page);
		return addPaginationModel(page, paginated, model);
	}
	
	/*
	 * ◯データをモデルに詰めてテンプレートを返却
	 */
	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		List<Vet> listVets = paginated.getContent();		// 中身のデータを取り出すメソッド
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());		// ページ総数を取り出すメソッド
		model.addAttribute("totalItems", paginated.getTotalElements());		// データ総数を取り出すメソッド
		model.addAttribute("listVets", listVets);
		return "vets/vetList";
	}
	
	/*
	 * ◯ページ単位のVetデータ取得
	 * ・PageRequest.of(page - 1, pageSize)：何ページ目を何件取得し、どの項目でソートするかなどの検索条件をまとめたPageable オブジェクトを返す
	 * ・第一引数にページ番号、第二引数にページサイズ　第三引数にソート（Sortオブジェクト）　
	 * 　例）Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());　※降順なら.descending()
	 * ・page - 1：SpringDataJPAではページの番号が 0 から始まるため -1
	 */
	private Page<Vet> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return vetRepository.findAll(pageable);
	}
}
