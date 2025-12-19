package com.techforb.apiportalrecruiting.core.config.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.techforb.apiportalrecruiting.core.dtos.ResponseDetailSkillDTO;
import com.techforb.apiportalrecruiting.core.dtos.LanguageDetailDTO;
import com.techforb.apiportalrecruiting.core.dtos.VacancyDetailsDTO;
import com.techforb.apiportalrecruiting.core.entities.DetailSkill;
import com.techforb.apiportalrecruiting.core.entities.Language;
import com.techforb.apiportalrecruiting.core.entities.Vacancy;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappersConfig {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean("mergerMapper")
	public ModelMapper mergerMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration()
				.setPropertyCondition(Conditions.isNotNull());
		return mapper;
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}

	@Bean
	public ModelMapperUtils modelMapperUtils() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		//Mapeo Language -> LanguageDetailDTO
		modelMapper.typeMap(Language.class, LanguageDetailDTO.class)
				.addMappings(mapper -> {
					mapper.map(src -> src.getLanguageLevel(),LanguageDetailDTO::setLanguageLevel);
					mapper.map(src -> src.getName(),LanguageDetailDTO::setName);
				});

		// Mapeo DetailSkill -> DetailSkillDTO
		modelMapper.typeMap(DetailSkill.class, ResponseDetailSkillDTO.class)
				.addMappings(mapper->{
					mapper.map(src->src.getSkill().getDescription(), ResponseDetailSkillDTO::setDescriptionSkill);
					mapper.map(src->src.getIsObligatory(), ResponseDetailSkillDTO::setIsObligatory);
					mapper.map(src->src.getPriority(), ResponseDetailSkillDTO::setPriority);
					mapper.map(src->src.getYearsExperience(), ResponseDetailSkillDTO::setYearsExperience);
				});

		// Mapeo  Vacancy -> VacancyDetailsDTO
		modelMapper.typeMap(Vacancy.class, VacancyDetailsDTO.class)
				.addMappings(mapper->{
					mapper.map(src->src.getCompany().getName(),VacancyDetailsDTO::setNameCompany);
					mapper.map(src->src.getRole(),VacancyDetailsDTO::setRole);
					mapper.map(src->src.getDescription(),VacancyDetailsDTO::setDescription);
				});

		return new ModelMapperUtils(modelMapper);
	}
}
