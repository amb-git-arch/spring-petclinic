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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test class for {@link TelephoneMasker}
 */
class TelephoneMaskerTests {

	private static final List<String> SEED_TELEPHONES = List.of("6085551023", "6085551749", "6085558763", "6085553198",
			"6085552765", "6085552654", "6085555387", "6085557683", "6085559435", "6085555487");

	private final TelephoneMasker masker = new TelephoneMasker();

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { "   ", "\t \n" })
	void missingTelephoneIsRenderedEmpty(String telephone) {
		assertThat(masker.mask(telephone)).isEmpty();
	}

	@ParameterizedTest
	@MethodSource("maskingCases")
	void masksAllButLastFourCharacters(String telephone, String expected) {
		assertThat(masker.mask(telephone)).isEqualTo(expected);
	}

	static Stream<Arguments> maskingCases() {
		return Stream.of(Arguments.of("6085551023", "******1023"), Arguments.of(" 6085551023 ", "******1023"),
				Arguments.of("12345", "*2345"), Arguments.of("1234", "****"), Arguments.of("12", "**"),
				Arguments.of("608-555-1023", "********1023"),
				Arguments.of("12345678901234567890", "****************7890"));
	}

	@Test
	void seedTelephonesKeepLengthAndLastFourDigitsOnly() {
		for (String telephone : SEED_TELEPHONES) {
			String masked = masker.mask(telephone);
			assertThat(masked).hasSameSizeAs(telephone)
				.isNotEqualTo(telephone)
				.endsWith(telephone.substring(telephone.length() - 4))
				.startsWith("******")
				.doesNotContain(telephone.substring(0, telephone.length() - 4));
		}
	}

}
