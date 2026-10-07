package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VisitController {

	private final OwnerRepository owners;
	
	public VisitController (OwnerRepository owners) {
		this.owners = owners;
	}
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id" );
	}
	
	/*
	 * ◯Optional<Owner>を使用する理由
	 * ・Spring Data JPAのfindById メソッドは戻り値の型が最初から Optional<Owner> に設計されている
	 * 　→データベースに該当のデータが存在しない可能性があるため
	 * ・データが存在しない可能性を明示するため
	 * 　→古いJavaではnullを直接返し、if（owner == null）を忘れがち＝ownerのメソッド呼び出しでアプリがヌルポで落ちがち
	 * ・処理を安全かつ簡潔に書くため
	 * 　→最終的に変数 owner には 絶対に null が入らない ことが保証されます
	 * 　　本来は1行でスッキリ書ける
	 * 　　wner owner = owners.findById(ownerId)
	 * 　　　.orElseThrow(() -> new IllegalArgumentException("Owner not found..."));
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
		
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		
		model.put("pet", pet);		// "pet"というキーでmodelに格納
		model.put("owner", owner);		// "owner"というキーでmodelに格納
		
		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;		// "visit"というキーでmodelに格納
	}
	
	/*
	 * ◯明日の日付をモデルに格納し画面に渡す
	 */
	@ModelAttribute("minVisitDate")
	public LocalDate minVisitDate() {
		return LocalDate.now().plusDays(1);
	}
	
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}
	
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if(visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate");
		}
		
		if(result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		
		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		
		return "redirect:/owners/{ownerId}";
		
	}
	
}
