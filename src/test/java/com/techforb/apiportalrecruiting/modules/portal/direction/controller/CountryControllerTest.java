package com.techforb.apiportalrecruiting.modules.portal.direction.controller;

import com.techforb.apiportalrecruiting.modules.portal.direction.dto.CountryItemDTO;
import com.techforb.apiportalrecruiting.modules.portal.direction.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void list_shouldReturnOkWithBody() {
        List<CountryItemDTO> data = List.of(
                CountryItemDTO.builder().id(1L).name("Argentina").build(),
                CountryItemDTO.builder().id(2L).name("Brasil").build()
        );
        when(countryService.listAll()).thenReturn(data);

        ResponseEntity<List<CountryItemDTO>> response = countryController.list();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Argentina");
    }
}
