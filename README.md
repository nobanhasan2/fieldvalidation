# FieldValidation
## _validate field from viewModel LiveData_
## Features

- Validate your textfield easyly from ViewModel with livedata observer.
- Add custom rule for customize your validation logic.


Add it in your root build.gradle at the end of repositories:
```bash
allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
}
```
Step 2. Add the dependency 
```bash
dependencies {
	   implementation 'com.github.nobanhasan2:fieldvalidation:v0.0.3-alpha'
}
```


## Usage

```bash
@HiltViewModel
class LoginViewModel
@Inject constructor(
        private val getLogin: GetLogin
): BaseViewModel(){

    private val _userResult: MutableLiveData<UserResult> = MutableLiveData()
    val userResult: LiveData<UserResult> = _userResult
    val usernameLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val usernameValidator = Validator.inputFieldValidate(FieldValidate.TEXT.name,usernameLiveData)
    val passwordValidator = Validator.inputFieldValidate(FieldValidate.PASSWORD.name,passwordLiveData)
    val isLoginFormValidMediator = MediatorLiveData<Boolean>()
    init {
        isLoginFormValidMediator.value = false
        isLoginFormValidMediator.addSource(usernameLiveData) { validateForm() }
        isLoginFormValidMediator.addSource(passwordLiveData) { validateForm() }
    }
    fun login(username:String,password:String){
        loadingStatus.value = true
        getLogin(GetLogin.Params(username,password),viewModelScope){
            it.fold(::handleFailure, ::handleOnSuccessLogin)
        }
    }

    private fun handleOnSuccessLogin(userResult: UserResult) {
        loadingStatus.value = false
        _userResult.value = userResult
    }
    private fun validateForm() {
        val validators = listOf(usernameValidator, passwordValidator)
        val validatorResolver = LiveDataValidatorResolver(validators)
        isLoginFormValidMediator.value = validatorResolver.isValid()
    }

}
```
