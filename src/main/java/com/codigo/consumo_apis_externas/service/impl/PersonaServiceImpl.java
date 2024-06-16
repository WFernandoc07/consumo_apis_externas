package com.codigo.consumo_apis_externas.service.impl;

import com.codigo.consumo_apis_externas.clients.ClientReniec;
import com.codigo.consumo_apis_externas.constants.Constantes;
import com.codigo.consumo_apis_externas.dao.PersonaRepository;
import com.codigo.consumo_apis_externas.entity.PersonaEntity;
import com.codigo.consumo_apis_externas.request.PersonaRequest;
import com.codigo.consumo_apis_externas.response.ResponseBase;
import com.codigo.consumo_apis_externas.response.ResponseReniec;
import com.codigo.consumo_apis_externas.retrofit.ReniecService;
import com.codigo.consumo_apis_externas.retrofit.client.ReniecCliente;
import com.codigo.consumo_apis_externas.service.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personaRepository;
    private final ClientReniec clientReniec;
    private final RestTemplate restTemplate;

    ReniecService apiService = ReniecCliente.getClient().create(ReniecService.class);

    @Value("${token.api}")
    private String tokenApi;

    @Override
    public ResponseBase crearPersona(PersonaRequest personaRequest) throws IOException {
        PersonaEntity personaEntity = getEntityRetrofit(personaRequest);
        if(personaEntity != null){
            personaRepository.save(personaEntity);
            return new ResponseBase(Constantes.CODIGO_EXITO, Constantes.MENSAJE_EXITO, Optional.of(personaEntity));
        }else {
            return new ResponseBase(Constantes.CODIGO_ERROR, Constantes.MENSAJE_ERROR, Optional.of(personaEntity));
        }
    }

    private ResponseReniec getExecutionReniec(String dni){
        String auth = "Bearer " + tokenApi;
        ResponseReniec reniec = clientReniec.getInfoReniec(dni, auth);
        return reniec;
    }

    private PersonaEntity getEntity(PersonaRequest personaRequest){
        PersonaEntity personaEntity = new PersonaEntity();

        ResponseReniec responseReniec = getExecutionReniec(personaRequest.getDni());
        if(responseReniec != null){
            personaEntity.setNombres(responseReniec.getNombres());
            personaEntity.setApellidoPaterno(responseReniec.getApellidoPaterno());
            personaEntity.setApellidoMaterno(responseReniec.getApellidoMaterno());
            personaEntity.setNumDoc(responseReniec.getNumeroDocumento());
            personaEntity.setTipoDoc(responseReniec.getTipoDocumento());
            personaEntity.setUsuaCrea(Constantes.AUDIT_ADMIN);
            personaEntity.setDateCrea(new Timestamp(System.currentTimeMillis()));
            return personaEntity;
        }else {
            return null;
        }
    }

    private PersonaEntity getEntityRetrofit(PersonaRequest personaRequest) throws IOException {
        PersonaEntity personaEntity = new PersonaEntity();
        Call<ResponseReniec> call = apiService.getDatosPersona("Bearer"+tokenApi, personaRequest.getDni());
        Response<ResponseReniec> ejecutandoConsulta = call.execute();
        if (ejecutandoConsulta.isSuccessful() && ejecutandoConsulta.body()!=null){
            ResponseReniec responseReniec = ejecutandoConsulta.body();

            personaEntity.setNombres(responseReniec.getNombres());
            personaEntity.setApellidoPaterno(responseReniec.getApellidoPaterno());
            personaEntity.setApellidoMaterno(responseReniec.getApellidoMaterno());
            personaEntity.setNumDoc(responseReniec.getNumeroDocumento());
            personaEntity.setTipoDoc(responseReniec.getTipoDocumento());
            personaEntity.setUsuaCrea(Constantes.AUDIT_ADMIN);
            personaEntity.setDateCrea(new Timestamp(System.currentTimeMillis()));
            return personaEntity;
        }else {
            return null;
        }
    }

    private PersonaEntity getEntityRestTemplate(PersonaRequest personaRequest){
        String url = "https://api.apis.net.pe/v2/reniec/dni?numero="+personaRequest.getDni();

        try {
            ResponseEntity<ResponseReniec> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(CreateHeaders(tokenApi)),
                    ResponseReniec.class
            );
            ResponseReniec responseReniec = response.getBody();

            PersonaEntity personaEntity = new PersonaEntity();

            personaEntity.setNombres(responseReniec.getNombres());
            personaEntity.setApellidoPaterno(responseReniec.getApellidoPaterno());
            personaEntity.setApellidoMaterno(responseReniec.getApellidoMaterno());
            personaEntity.setNumDoc(responseReniec.getNumeroDocumento());
            personaEntity.setTipoDoc(responseReniec.getTipoDocumento());
            personaEntity.setUsuaCrea(Constantes.AUDIT_ADMIN);
            personaEntity.setDateCrea(new Timestamp(System.currentTimeMillis()));
            return personaEntity;

        }catch (HttpClientErrorException e){
            System.out.println("Error al consumir Ap API externa" + e.getStatusCode());
        }
        return null;
    }

    private Object CreateHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
