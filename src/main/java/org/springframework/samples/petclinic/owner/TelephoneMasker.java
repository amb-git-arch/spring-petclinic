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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Masks an owner's telephone number for views that list many owners, so that only the
 * last four characters are shown. Views showing a single owner display the full number.
 * <p>
 * Masking is on by default. Setting {@code petclinic.privacy.mask-telephones=false} turns
 * it off for support diagnostics, and list views then show the full number.
 * <p>
 * The bean name is fixed because templates call it as {@code @telephoneMasker}.
 */
@Component("telephoneMasker")
public class TelephoneMasker {

	private static final int VISIBLE_CHARACTERS = 4;

	private static final char MASK_CHARACTER = '*';

	private final boolean maskTelephones;

	public TelephoneMasker(@Value("${petclinic.privacy.mask-telephones:true}") boolean maskTelephones) {
		this.maskTelephones = maskTelephones;
	}

	/**
	 * Mask all but the last four characters of the given telephone number, unless masking
	 * is turned off, in which case the value is returned unchanged.
	 * @param telephone the telephone number, may be {@code null}
	 * @return the input unchanged when masking is off; otherwise the masked value with
	 * the same length as the stripped input, an empty string for {@code null} or blank
	 * input, or a fully masked value when the input has four or fewer characters
	 */
	public String mask(String telephone) {
		if (!this.maskTelephones) {
			return telephone;
		}
		if (telephone == null || telephone.isBlank()) {
			return "";
		}
		String value = telephone.strip();
		int visible = value.length() > VISIBLE_CHARACTERS ? VISIBLE_CHARACTERS : 0;
		int masked = value.length() - visible;
		return String.valueOf(MASK_CHARACTER).repeat(masked) + value.substring(masked);
	}

}
