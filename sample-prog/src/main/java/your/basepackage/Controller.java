package your.basepackage;

import org.example.server.PetApi;
import org.example.server.RestCategory;
import org.example.server.RestPet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class Controller implements PetApi {

    @Override
    public ResponseEntity<RestPet> getPetById(Long petId) {
        System.out.println("found pet #" + petId);
        RestPet pet = new RestPet().id(petId).name("pet#" + petId).status(RestPet.StatusEnum.PENDING)
                .category(new RestCategory().id(1l).name("unique category"));
        return ResponseEntity.ok(pet);
    }
}


