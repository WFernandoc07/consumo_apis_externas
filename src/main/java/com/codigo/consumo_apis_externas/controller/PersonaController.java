package com.codigo.consumo_apis_externas.controller;

import com.codigo.consumo_apis_externas.constants.Constantes;
import com.codigo.consumo_apis_externas.request.PersonaRequest;
import com.codigo.consumo_apis_externas.response.ResponseBase;
import com.codigo.consumo_apis_externas.service.PersonaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("v1/api/persona")
public class PersonaController {
    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PostMapping()
    public ResponseEntity<ResponseBase> crearPersona(@RequestBody PersonaRequest personaRequest) throws IOException {
        ResponseBase responseBase = personaService.crearPersona(personaRequest);

        if(responseBase.getCode() == Constantes.CODIGO_EXITO){
            return ResponseEntity.ok(responseBase);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBase);
        }
    }
}
