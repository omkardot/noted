package com.omkar.noted.Fragements

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.omkar.noted.Genaric.NotedSharedPreference
import com.omkar.noted.R
import com.omkar.noted.View.PoastGerenratorInput


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private var RC_SIGN_IN: Int = 1001
    private var isSignUpMode = false

    // View references
    private lateinit var loginTitle: TextView
    private lateinit var loginSubtitle: TextView
    private lateinit var errorMessageText: TextView
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var passwordStrengthLayout: LinearLayout
    private lateinit var passwordStrengthText: TextView
    private lateinit var strengthIndicator1: View
    private lateinit var strengthIndicator2: View
    private lateinit var strengthIndicator3: View
    private lateinit var strengthIndicator4: View
    private lateinit var termsCheckbox: CheckBox
    private lateinit var forgotPasswordText: TextView
    private lateinit var loginButton: Button
    private lateinit var loginProgressBar: ProgressBar
    private lateinit var orDividerLayout: LinearLayout
    private lateinit var googleSignUpButton: Button
    private lateinit var linkedinSignUpButton: Button
    private lateinit var authSwitchPrompt: TextView
    private lateinit var authSwitchAction: TextView
    private lateinit var prefs: NotedSharedPreference

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext())
        }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        prefs = NotedSharedPreference(view.context)

        // Google sign-in config
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Initialize all views
        initializeViews(view)

        // Setup UI
        setupInitialMode()
        setupListeners()
        setupPasswordStrengthMonitor()
        setupTermsAndConditions()

        return view
    }

    private fun initializeViews(view: View) {
        // Text Views
        loginTitle = view.findViewById(R.id.login_title)
        loginSubtitle = view.findViewById(R.id.login_subtitle)
        errorMessageText = view.findViewById(R.id.error_message_text)
        passwordStrengthText = view.findViewById(R.id.password_strength_text)
        forgotPasswordText = view.findViewById(R.id.forgot_password_text)
        authSwitchPrompt = view.findViewById(R.id.auth_switch_prompt)
        authSwitchAction = view.findViewById(R.id.auth_switch_action)

        // Input Layouts
        nameInputLayout = view.findViewById(R.id.name_input_layout)
        emailInputLayout = view.findViewById(R.id.email_input_layout)
        passwordInputLayout = view.findViewById(R.id.password_input_layout)
        confirmPasswordInputLayout = view.findViewById(R.id.confirm_password_input_layout)

        // Edit Texts
        nameEditText = view.findViewById(R.id.name_edit_text)
        emailEditText = view.findViewById(R.id.email_edit_text)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text)

        // Password Strength Indicators
        passwordStrengthLayout = view.findViewById(R.id.password_strength_layout)
        strengthIndicator1 = view.findViewById(R.id.strength_indicator_1)
        strengthIndicator2 = view.findViewById(R.id.strength_indicator_2)
        strengthIndicator3 = view.findViewById(R.id.strength_indicator_3)
        strengthIndicator4 = view.findViewById(R.id.strength_indicator_4)

        // Buttons and Controls
        termsCheckbox = view.findViewById(R.id.terms_checkbox)
        loginButton = view.findViewById(R.id.login_button)
        loginProgressBar = view.findViewById(R.id.login_progress_bar)
        googleSignUpButton = view.findViewById(R.id.google_sign_up_button)
        linkedinSignUpButton = view.findViewById(R.id.linkedin_sign_up_button)

        // Layouts
        orDividerLayout = view.findViewById(R.id.or_divider_layout)
    }

    private fun setupInitialMode() {
        // Start in login mode
        updateUIForMode(isSignUpMode = false)
    }

    private fun setupListeners() {
        // Auth mode switch (Sign Up / Log In toggle)
        authSwitchAction.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUIForMode(isSignUpMode)
            clearErrors()
        }

        // Main action button (Login / Sign Up)
        loginButton.setOnClickListener {
            if (isSignUpMode) {
                handleSignUp()
            } else {
                handleLogin()
            }
        }

        // Forgot password
        forgotPasswordText.setOnClickListener {
            handleForgotPassword()
        }

        // Social sign up buttons
        googleSignUpButton.setOnClickListener {
            signInWithGoogle()
        }

        linkedinSignUpButton.setOnClickListener {
            handleLinkedInSignUp()
        }

        // Clear error on text change
        setupErrorClearingListeners()
    }

    private fun setupErrorClearingListeners() {
        nameEditText.addTextChangedListener(createErrorClearWatcher(nameInputLayout))
        emailEditText.addTextChangedListener(createErrorClearWatcher(emailInputLayout))
        passwordEditText.addTextChangedListener(createErrorClearWatcher(passwordInputLayout))
        confirmPasswordEditText.addTextChangedListener(createErrorClearWatcher(confirmPasswordInputLayout))
    }

    private fun createErrorClearWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputLayout.error = null
                errorMessageText.visibility = View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun setupPasswordStrengthMonitor() {
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isSignUpMode) {
                    updatePasswordStrength(s.toString())
                }
            }
        })
    }

    private fun setupTermsAndConditions() {
        val termsText = Html.fromHtml(getString(R.string.terms_conditions), Html.FROM_HTML_MODE_LEGACY)
        termsCheckbox.text = termsText
        termsCheckbox.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun updateUIForMode(isSignUpMode: Boolean) {
        if (isSignUpMode) {
            // Sign Up Mode
            loginTitle.text = getString(R.string.create_account)
            loginSubtitle.text = getString(R.string.sign_up_subtitle)
            loginButton.text = getString(R.string.btn_sign_up)
            authSwitchPrompt.text = getString(R.string.already_have_account)
            authSwitchAction.text = getString(R.string.log_in_action)

            // Show sign up specific fields
            nameInputLayout.visibility = View.VISIBLE
            confirmPasswordInputLayout.visibility = View.VISIBLE
            passwordStrengthLayout.visibility = View.VISIBLE
            passwordStrengthText.visibility = View.VISIBLE
            termsCheckbox.visibility = View.VISIBLE
            orDividerLayout.visibility = View.VISIBLE
            googleSignUpButton.visibility = View.VISIBLE
            linkedinSignUpButton.visibility = View.VISIBLE

            // Hide login specific fields
            forgotPasswordText.visibility = View.GONE

        } else {
            // Login Mode
            loginTitle.text = getString(R.string.welcome_back)
            loginSubtitle.text = getString(R.string.sign_in_subtitle)
            loginButton.text = getString(R.string.btn_log_in)
            authSwitchPrompt.text = getString(R.string.dont_have_account)
            authSwitchAction.text = getString(R.string.sign_up_action)

            // Hide sign up specific fields
            nameInputLayout.visibility = View.GONE
            confirmPasswordInputLayout.visibility = View.GONE
            passwordStrengthLayout.visibility = View.GONE
            passwordStrengthText.visibility = View.GONE
            termsCheckbox.visibility = View.GONE
            orDividerLayout.visibility = View.GONE
            googleSignUpButton.visibility = View.GONE
            linkedinSignUpButton.visibility = View.GONE

            // Show login specific fields
            forgotPasswordText.visibility = View.VISIBLE
        }

        // Clear all fields when switching modes
        clearAllFields()
    }

    private fun handleLogin() {
        if (!validateLoginInputs()) {
            return
        }

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        signInWithEmail(email, password)
    }

    private fun handleSignUp() {
        if (!validateSignUpInputs()) {
            return
        }

        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        showLoading(true)

        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // Update profile with name
                    val user = auth.currentUser
                    saveUserToFirestore(user, "email", name)
                } else {
                    showError("Sign up failed: ${task.exception?.message}")
                }
            }
    }

    private fun handleForgotPassword() {
        val email = emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            showError("Please enter your email address")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    showError("Failed to send reset email: ${task.exception?.message}")
                }
            }
    }

    private fun handleLinkedInSignUp() {
        showError("LinkedIn Sign-In is not yet implemented")
    }

    private fun validateLoginInputs(): Boolean {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        var isValid = true

        if (email.isEmpty()) {
            emailInputLayout.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        }

        return isValid
    }

    private fun validateSignUpInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        var isValid = true

        if (name.isEmpty()) {
            nameInputLayout.error = getString(R.string.error_empty_name)
            isValid = false
        }

        if (email.isEmpty()) {
            emailInputLayout.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 8) {
            passwordInputLayout.error = getString(R.string.error_short_password)
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = getString(R.string.error_passwords_mismatch)
            isValid = false
        }

        if (!termsCheckbox.isChecked) {
            showError(getString(R.string.error_terms_not_accepted))
            isValid = false
        }

        return isValid
    }

    private fun updatePasswordStrength(password: String) {
        val strength = calculatePasswordStrength(password)

        // Reset all indicators
        strengthIndicator1.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        strengthIndicator2.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        strengthIndicator3.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
        strengthIndicator4.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))

        when (strength) {
            1 -> {
                strengthIndicator1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_weak))
                passwordStrengthText.text = getString(R.string.password_strength_weak)
                passwordStrengthText.setTextColor(ContextCompat.getColor(requireContext(), R.color.password_weak))
            }
            2 -> {
                strengthIndicator1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_fair))
                strengthIndicator2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_fair))
                passwordStrengthText.text = getString(R.string.password_strength_fair)
                passwordStrengthText.setTextColor(ContextCompat.getColor(requireContext(), R.color.password_fair))
            }
            3 -> {
                strengthIndicator1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_good))
                strengthIndicator2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_good))
                strengthIndicator3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_good))
                passwordStrengthText.text = getString(R.string.password_strength_good)
                passwordStrengthText.setTextColor(ContextCompat.getColor(requireContext(), R.color.password_good))
            }
            4 -> {
                strengthIndicator1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_strong))
                strengthIndicator2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_strong))
                strengthIndicator3.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_strong))
                strengthIndicator4.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.password_strong))
                passwordStrengthText.text = getString(R.string.password_strength_strong)
                passwordStrengthText.setTextColor(ContextCompat.getColor(requireContext(), R.color.password_strong))
            }
            else -> {
                passwordStrengthText.text = ""
            }
        }
    }

    private fun calculatePasswordStrength(password: String): Int {
        if (password.isEmpty()) return 0

        var strength = 0

        when {
            password.length >= 12 -> strength += 2
            password.length >= 8 -> strength += 1
        }

        if (password.any { it.isLowerCase() }) strength++
        if (password.any { it.isUpperCase() }) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++

        return when {
            strength <= 2 -> 1
            strength <= 4 -> 2
            strength <= 5 -> 3
            else -> 4
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loginButton.isEnabled = !isLoading
        loginButton.text = if (isLoading) "" else {
            if (isSignUpMode) getString(R.string.btn_sign_up) else getString(R.string.btn_log_in)
        }
        loginProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        emailEditText.isEnabled = !isLoading
        passwordEditText.isEnabled = !isLoading
        nameEditText.isEnabled = !isLoading
        confirmPasswordEditText.isEnabled = !isLoading
        googleSignUpButton.isEnabled = !isLoading
        linkedinSignUpButton.isEnabled = !isLoading
        authSwitchAction.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        errorMessageText.text = message
        errorMessageText.visibility = View.VISIBLE
    }

    private fun clearErrors() {
        errorMessageText.visibility = View.GONE
        nameInputLayout.error = null
        emailInputLayout.error = null
        passwordInputLayout.error = null
        confirmPasswordInputLayout.error = null
    }

    private fun clearAllFields() {
        nameEditText.text?.clear()
        emailEditText.text?.clear()
        passwordEditText.text?.clear()
        confirmPasswordEditText.text?.clear()
        termsCheckbox.isChecked = false
        clearErrors()
    }

    // Firebase Authentication Methods

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    saveUserToFirestore(auth.currentUser, "email")
                } else {
                    showError("Login failed: ${task.exception?.message}")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: Exception) {
                showError("Google sign-in failed: ${e.message}")
                Log.e(TAG, "Google sign-in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoading(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            showLoading(false)
            if (task.isSuccessful) {
                saveUserToFirestore(auth.currentUser, "google")
            } else {
                showError("Google login failed: ${task.exception?.message}")
            }
        }
    }

    private fun saveUserToFirestore(user: FirebaseUser?, provider: String, displayName: String? = null) {
        if (user == null) return

        val userRef = firestore.collection("login_details").document(user.uid)

        userRef.get().addOnSuccessListener { document ->
            val now = FieldValue.serverTimestamp()
            if (document.exists()) {
                // Update last login
                userRef.update("lastLogin", now).addOnSuccessListener {
                    navigateToHome()
                }
            } else {
                // Create new record
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "name" to (displayName ?: user.displayName),
                    "email" to user.email,
                    "provider" to provider,
                    "createdAt" to now,
                    "lastLogin" to now,
                    "profilePicUrl" to user.photoUrl?.toString()
                )
                userRef.set(userData).addOnSuccessListener {
                    navigateToHome()
                }
            }
        }.addOnFailureListener { e ->
            showError("Failed to save user data: ${e.message}")
        }
    }

    private fun navigateToHome() {
        // Navigate to your home fragment
        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(),PoastGerenratorInput::class.java))
        prefs.saveString("username", "Omkar")
        prefs.saveBoolean("isLoggedIn", true)
        // Example navigation - adjust based on your app structure
        // val homeFragment = HomeFragment()
        // parentFragmentManager.beginTransaction()
        //     .replace(R.id.fragment_container, homeFragment)
        //     .commit()
    }
}


