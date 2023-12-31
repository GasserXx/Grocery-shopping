package com.umega.grocery.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.User
import kotlinx.coroutines.runBlocking

class LoginViewModel(private val navController: NavController) : ViewModel() {
    var email: String = ""
    var password: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var phoneNumber: String = ""

    val emailError: MutableLiveData<String?> = MutableLiveData(null)
    val passwordError: MutableLiveData<String?> = MutableLiveData(null)
    val firstNameError: MutableLiveData<String?> = MutableLiveData(null)
    val lastNameError: MutableLiveData<String?> = MutableLiveData(null)
    val phoneNumberError: MutableLiveData<String?> = MutableLiveData(null)


    private val hasher = Hasher()
    private var hashedPassword: String? = ""

    private var repo:Repo? = null

    private val _response: MutableLiveData<Int> = MutableLiveData(-3)
    val response: LiveData<Int>
        get() = _response

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String>
        get() = _messageLiveData

    private val _authenticated = MutableLiveData<Boolean>()
    val authenticated: LiveData<Boolean>
        get() = _authenticated

    //clearance
    private fun clearCache(){
        email  = ""
        password  = ""
        firstName  = ""
        lastName  = ""
        phoneNumber  = ""
        emailError.value  = null
        passwordError.value= null
        firstNameError.value = null
        lastNameError.value= null
        phoneNumberError.value = null
        _response.value = -3
    }
    //setting repo on launch
    fun setRepo(repoObject: Repo){
        repo = repoObject
    }

    //Are we still signed in
    fun checkUserIdStorage():Boolean = repo!!.getUserID() > 0

    //Validation
    private fun isEmailValid(signIn: Boolean): Boolean = if (email.isBlank()) {
        emailError.value = emailError_invalid_string
        false
    } else if (!signIn && !email.matches(emailRegex)) {
        emailError.value = emailError_form_string
        false
    } else
        true

    private fun isPasswordValidSignUp(): Boolean =
        if (password.length < 8) {
            passwordError.value = passwordError_largerThan8_string
            false
        } else if (password.any { it.isUpperCase() }) {
            passwordError.value = passwordError_containsUpperLetters_string
            false
        } else if (password.any { it.isLowerCase() }) {
            passwordError.value = passwordError_containsLowerLetters_string
            false
        } else
            true

    private fun isPasswordValidSignIn(): Boolean =
        if (password.isBlank()) {
            passwordError.value = passwordError_invalid_string
            false
        } else
            true

    private fun isFirstNameValid(): Boolean =
        if (firstName.isBlank() || firstName.any { it.isDigit() }) {
            firstNameError.value = invalidInputError_string
            false
        } else
            true

    private fun isLastNameValid(): Boolean =
        if (lastName.isBlank() || lastName.any { it.isDigit() }) {
            lastNameError.value = invalidInputError_string
            false
        } else
            true

    private fun isPhoneNumberValid(): Boolean =
        if (phoneNumber.isBlank()) {
            phoneNumberError.value = invalidInputError_string
            false
        } else if (!phoneNumber.matches(phoneRegex)) {
            phoneNumberError.value = phoneNumberError_invalid_string
            false
        }
        else if (phoneNumber.length != 11) {
            phoneNumberError_shouldBe11_string
            false
        }
        else
            true


    //didn't simplify the following to make sure that all validation are processed even when some Validation returns false
    private fun validate(signIn: Boolean): Boolean {
        var result = isEmailValid(signIn)
        if (signIn) {
            result = result && isPasswordValidSignIn()
        } else {
            result = isPasswordValidSignUp() && result
            result = isFirstNameValid() && result
            result = isLastNameValid() && result
            result = isPhoneNumberValid() && result
        }
        return result
    }

    fun login() {
        //validate input
        if (!validate(signIn = true))
            return
        //show loading
        showLoading()
        //hashPassword
        hashedPassword = hasher.hashPassword(password)
        //authenticate
        repo!!.authentication(User(email, hashedPassword!!, null, null, null), _response)
    }

    fun afterLogin() {
        //giveFeedBack
        hideLoading()
        when (_response.value) {
            200 -> {
                runBlocking {
                    repo!!.storeEmail(email)
                    //Store userID and flag end activity
                    repo!!.storeUserID()
                    signInSuccess()
                    clearCache()
                    _authenticated.value = true
                }
                return
            }

            501 -> emailError.value = emailError_invalid_string
            401 -> passwordError.value = passwordError_invalid_string
        }
        signInFailed()
        return
    }


    fun signUp(){
        //validate input
        if (!validate(signIn = false))
            return
        //show loading
        showLoading()
        //hashPassword
        hashedPassword = hasher.hashPassword(password)
        //registerUser
        repo!!.registerUser(User(email,hashedPassword!!,firstName,lastName, phoneNumber),_response)
    }

    fun afterSignUp() {
        //giveFeedBack
        hideLoading()
        when(_response.value) {
            200 -> {
                //on success navigate to transition
                navigateSignUpToTransition()
                clearCache()
                signUpSuccess()
                return
            }
            501-> invalidEmailNotice()
            400-> signUpFailed()
        }
        return
    }
    //Loading functions
    private fun showLoading() {
        _loading.value = true
    }

    private fun hideLoading() {
        _loading.value = false
    }

    //Toasts Fns
    private fun signInFailed() {
        raiseToast(loginError_failed)
    }

    private fun signInSuccess() {
        raiseToast(loginError_success)
    }
    private fun signUpFailed() {
        raiseToast(registrationError_failed)
    }

    private fun signUpSuccess() {
        raiseToast(registrationError_success)
    }

    private fun invalidEmailNotice() {
        raiseToast(emailError_invalid_string)
    }
    //show Toast on the Screen
    private fun raiseToast(message:String) {
        _messageLiveData.value = message
    }
    //Navigation fns to be attached through 2 way dataBinding
    fun navigateLoginToSignIn(){
        navController.navigate(R.id.action_loginFragment2_to_signInFragment)
    }
    fun navigateLoginToSignUp(){
        navController.navigate(R.id.action_loginFragment2_to_signUpFragment)
    }
    fun navigateTransitionToSignIn(){
        navController.navigate(R.id.action_transistionFragment_to_signInFragment)
    }
    private fun navigateSignUpToTransition(){
        navController.navigate(R.id.action_signUpFragment_to_transistionFragment)
    }
    fun navigateUp(){
        navController.navigateUp()
    }
    fun navigateTransitionToLogin (){
        navController.navigate(R.id.action_transistionFragment_to_loginFragment2)
    }
    fun navigateSignInToUnderConstruction (){
        navController.navigate(R.id.action_signInFragment_to_underConstructionFragment2)
    }
    fun navigateSignInToSignUP (){
        navController.navigate(R.id.action_signInFragment_to_signUpFragment)
    }
    fun navigateSignUPToSignIn (){
        navController.navigate(R.id.action_signUpFragment_to_signInFragment)
    }


    companion object{
        const val emailError_form_string:String = "It must be in form example@something.com"
        const val emailError_invalid_string:String = "Invalid Email"

        const val passwordError_largerThan8_string:String = "Password must be at least 8 characters"
        const val passwordError_containsLowerLetters_string:String = "Password must contains lowerLetters"
        const val passwordError_containsUpperLetters_string:String = "Password must contains upperLetters"
        const val passwordError_invalid_string:String = "Invalid password"

        const val invalidInputError_string:String = "Invalid Input"

        const val phoneNumberError_invalid_string:String = "Must be in form 01*********"
        const val phoneNumberError_shouldBe11_string:String = "Must contain 11 numbers"

        const val registrationError_failed:String = "Registration Failed"
        const val registrationError_success:String = "Registration Successful"

        const val loginError_failed:String = "Login Failed"
        const val loginError_success:String = "Logged In Successfully"

        val emailRegex:Regex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}".toRegex()
        val phoneRegex:Regex = "01\\d{9}".toRegex()

    }
}