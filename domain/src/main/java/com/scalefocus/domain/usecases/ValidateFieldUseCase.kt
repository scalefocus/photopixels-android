package com.scalefocus.domain.usecases

import com.scalefocus.domain.validation.PixelsFieldValidator
import com.scalefocus.domain.validation.ValidationRules
import javax.inject.Inject

class ValidateFieldUseCase @Inject constructor() {
    suspend fun invoke(value: String, validationRules: ValidationRules): Boolean {
        return PixelsFieldValidator.isFieldValid(value, validationRules)
    }
}
