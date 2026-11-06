/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests {@link OwnerController} views with telephone masking turned off for support
 * diagnostics ({@code petclinic.privacy.mask-telephones=false}).
 */
@WebMvcTest(OwnerController.class)
@Import(TelephoneMasker.class)
@TestPropertySource(properties = "petclinic.privacy.mask-telephones=false")
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTelephoneDiagnosticsTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private static Owner owner(int id, String firstName, String lastName, String telephone) {
		Owner owner = new Owner();
		owner.setId(id);
		owner.setFirstName(firstName);
		owner.setLastName(lastName);
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone(telephone);
		return owner;
	}

	@Test
	void ownersListShowsFullTelephoneWhenMaskingIsOff() throws Exception {
		given(this.owners.findByLastNameStartingWith(eq(""), any(Pageable.class))).willReturn(new PageImpl<>(
				List.of(owner(1, "George", "Franklin", "6085551023"), owner(2, "Betty", "Davis", "6085551749"))));

		mockMvc.perform(get("/owners?page=1"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(content().string(containsString("6085551023")))
			.andExpect(content().string(containsString("6085551749")))
			.andExpect(content().string(not(containsString("******"))));
	}

	@Test
	void ownerDetailsStillShowsFullTelephoneWhenMaskingIsOff() throws Exception {
		given(this.owners.findById(1)).willReturn(Optional.of(owner(1, "George", "Franklin", "6085551023")));

		mockMvc.perform(get("/owners/{ownerId}", 1))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownerDetails"))
			.andExpect(content().string(containsString("6085551023")));
	}

}
