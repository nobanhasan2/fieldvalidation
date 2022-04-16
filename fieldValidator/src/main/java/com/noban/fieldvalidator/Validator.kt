package com.noban.fieldvalidator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.noban.fieldvalidator.enums.FieldValidate


typealias Predicate = (value: String?) -> Boolean
class Validator {
    companion object{
        fun inputFieldValidate( type :String,value:LiveData<String>):LiveDataValidator{
            return when(type){
                FieldValidate.TEXT.name -> validateTextField(value)
                FieldValidate.PASSWORD.name -> validatePasswordField(value)
                FieldValidate.PHONE.name -> return validatePhoneField(value)
                FieldValidate.EMAIL.name -> return validateEmailField(value)
                else ->{ LiveDataValidator(value) }

            }
        }
        private fun validateTextField(value: LiveData<String>) :LiveDataValidator{
            return LiveDataValidator(value).apply {
                addRule("This field is required") { it.isNullOrBlank() }
            }
        }
        private fun validatePasswordField(value: LiveData<String>): LiveDataValidator {
            return LiveDataValidator(value).apply {
                addRule("password is required") { it.isNullOrBlank() }
                addRule("password length must be 10") { it?.length != 3 }
            }
        }
        private fun validatePhoneField(value: LiveData<String>): LiveDataValidator {
            return LiveDataValidator(value).apply {
                addRule("Phone is required") {
                    it.isNullOrBlank()
                }
            }
        }
        private fun validateEmailField(value: LiveData<String>): LiveDataValidator {
            return LiveDataValidator(value).apply {
                addRule("Email is required") { it.isNullOrBlank() }
            }
        }
    }





}

class LiveDataValidator(private val liveData: LiveData<String>) {
    private val validationRules = mutableListOf<Predicate>()
    private val errorMessages = mutableListOf<String>()

    private var error = MutableLiveData<String?>()

    //For checking if the liveData value matches the error condition set in the validation rule predicate
    //The livedata value is said to be valid when its value doesn't match an error condition set in the predicate
    fun isValid(): Boolean {
        for (i in 0 until validationRules.size) {
            if (validationRules[i](liveData.value)) {
                emitErrorMessage(errorMessages[i])
                return false
            }
        }

        emitErrorMessage(null)
        return true
    }

    //For emitting error messages
    private fun emitErrorMessage(messageRes: String?) {
        error.value = messageRes
    }

    //For adding validation rules
    fun addRule(errorMsg: String, predicate: Predicate) {
        validationRules.add(predicate)
        errorMessages.add(errorMsg)
    }
}

class LiveDataValidatorResolver(private val validators: List<LiveDataValidator>) {
    fun isValid(): Boolean {
        for (validator in validators) {
            if (!validator.isValid()) return false
        }
        return true
    }
}