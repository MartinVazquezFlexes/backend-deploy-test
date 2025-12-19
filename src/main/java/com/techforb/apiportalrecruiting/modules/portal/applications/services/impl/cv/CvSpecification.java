package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl.cv;

import com.techforb.apiportalrecruiting.core.entities.City;
import com.techforb.apiportalrecruiting.core.entities.Country;
import com.techforb.apiportalrecruiting.core.entities.Cv;
import com.techforb.apiportalrecruiting.core.entities.Direction;
import com.techforb.apiportalrecruiting.core.entities.Person;
import com.techforb.apiportalrecruiting.core.entities.Province;
import com.techforb.apiportalrecruiting.core.entities.Skill;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CvSpecification {
	public static Specification<Cv> hasCountryLike(String countryName) {
		return (rootCv, query, criteriaBuilder) -> {
			if (countryName == null || countryName.isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			Join<Cv, Person> personJoin = rootCv.join("person");
			Join<Person, Direction> directionJoin = personJoin.join("direction");
			Join<Direction, City> cityJoin = directionJoin.join("city");
			Join<City, Province> provinceJoin = cityJoin.join("province");
			Join<Province, Country> countryJoin = provinceJoin.join("country");

			return criteriaBuilder.like(criteriaBuilder.lower(countryJoin.get("name")), countryName.toLowerCase() + "%");
		};
	}

	public static Specification<Cv> hasSkill(String skill) {
		return (root, query, criteriaBuilder) -> {
			if (skill == null || skill.isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			Join<Cv, Person> personJoin = root.join("person");
			Join<Person, Skill> skillJoin = personJoin.join("skills");

			return criteriaBuilder.like(criteriaBuilder.lower(skillJoin.get("description")), skill.toLowerCase() + "%");
		};
	}

	public static Specification<Cv> hasIsLast(Boolean isLast){
		return (root, query, criteriaBuilder) -> {
			if(isLast==null||!isLast){
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.equal(root.get("isLast"), isLast);
		};
	}

	public static Specification<Cv> hasIdPerson(Long idPerson){
		return (root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("person").get("id"),idPerson);

	}
}
