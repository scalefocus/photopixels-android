package io.photopixels.domain.usecases

import io.photopixels.domain.validation.PixelsFieldValidator
import io.photopixels.domain.validation.ValidationRules
import javax.inject.Inject

class ValidateFieldUseCase @Inject constructor() {
    suspend fun invoke(value: String, validationRules: ValidationRules): Boolean {
        return PixelsFieldValidator.isFieldValid(value, validationRules)
    }
}
