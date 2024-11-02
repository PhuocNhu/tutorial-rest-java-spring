package com.example.tutorialrestjavaspring.api.controller.user;

import com.example.tutorialrestjavaspring.api.model.DataChange;
import com.example.tutorialrestjavaspring.model.Address;
import com.example.tutorialrestjavaspring.model.LocalUser;
import com.example.tutorialrestjavaspring.model.dao.AddressDAO;
import com.example.tutorialrestjavaspring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private AddressDAO addressDAO;

    private SimpMessagingTemplate messagingTemplate;

    private UserService userService;

    public UserController(AddressDAO addressDAO, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.addressDAO = addressDAO;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress
            (@AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {
        if(!userService.userHasPermissionToUser(user,userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressDAO.findByUser_Id(userId));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress
            (@AuthenticationPrincipal LocalUser user,
            @PathVariable Long userId,
            @RequestBody Address address){
        if(!userService.userHasPermissionToUser(user,userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        Address savedAddress = addressDAO.save(address);
        messagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                new DataChange<>(DataChange.ChangeType.INSERT, address));
        return ResponseEntity.ok(savedAddress);
    }

    @PatchMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress
            (@AuthenticationPrincipal LocalUser user,
             @PathVariable Long userId,
             @PathVariable Long addressId,
             @RequestBody Address address){
        //check if user login is same as user of the path variable passed in
        if(!userService.userHasPermissionToUser(user,userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        //check if addressId path variable is equal to the address request body
        if(addressId.equals(address.getId())){
            Optional<Address> opOrgAddress = addressDAO.findById(addressId);
            //if so then check the appearance of the address in DB
            if(opOrgAddress.isPresent()){
                LocalUser orgUser = opOrgAddress.get().getUser();
                //finally check the userId of address return from DB with userId from path variable
                if(orgUser.getId().equals(userId)){
                    address.setUser(orgUser);
                    Address savedAddress = addressDAO.save(address);
                    messagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                            new DataChange<>(DataChange.ChangeType.UPDATE, address));
                    return ResponseEntity.ok(savedAddress);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }



}
