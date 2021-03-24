package com.tesk.task.providers.api.impl.utils

import com.tesk.task.providers.api.AuthorizeException
import com.tesk.task.providers.api.EndpointGit
import com.tesk.task.providers.api.IHttpClient
import com.tesk.task.providers.api.impl.models.User
import java.util.regex.Matcher
import java.util.regex.Pattern

object AuthHelper {
    private val COMMIT_FORM_DATA = "commit"
    private val TOKEN_FORM_DATA = "authenticity_token"
    private val LOGIN_FORM_DATA = "login"
    private val PASSWORD_FORM_DATA = "password"
    private val TRUSTED_DEVICE = "trusted_device"
    private val WEBAUTHN_SUPPORTED = "webauthn-support"
    private val WEBAUTHN_IUVPAA_SUPPORT = "webauthn-iuvpaa-support"
    private val RETURN_TO = "return_to"
    private val ALLOW_SIGNUP = "allow_signup"
    private val CLIENT_ID = "client_id"
    private val INTEGRATION = "integration"
    private val REQUIRED_FIELD_5F9D = "required_field_5f9d"
    private val TIMESTAMP = "timestamp"
    private val TIMESTAMP_SECRET = "timestamp_secret"

    private val COMMIT_VALUE = "Sign in"
    private val WEBAUTHN_SUPPORTED_VALUE = "supported"
    private val WEBAUTHN_IUVPAA_SUPPORT_VALUE = "unsupported"
    private val RETURN_TO_VALUE = "/login/oauth/authorize"

    private val CONTENT_TYPE = "content-type"
    private val CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded"
    private val REFERED = "referer"
    private val REFERED_VALUE = "https://github.com/login"
    private val USER_AGENT = "user-agent"
    private val USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.111 YaBrowser/21.2.1.107 Yowser/2.5 Safari/537.36"

    private val TEMPLATE_TOKEN = "name=\"authenticity_token\" value=\"[a-z[0-9][/, =, +][A-Z]]*\""
    private val TEMPLATE_TIMESTAMP = "name=\"timestamp\" value=\"[0-9]*\""
    private val TEMPLATE_TIMESTAMP_SECRET = "name=\"timestamp_secret\" value=\"[0-9[a-z]]*\""

    private val TEMPLATE_TOKEN_TAKE_A_BITE = "name=\"authenticity_token\" value=\""
    private val TEMPLATE_TIMESTAMP_TAKE_A_BITE = "name=\"timestamp\" value=\""
    private val TEMPLATE_TIMESTAMP_SECRET_TAKE_A_BITE = "name=\"timestamp_secret\" value=\""
    private val CLOSABLE_TAKE_A_BITE = "\""

    private val TEMPLATE_USER = "meta name=\"user-login\" content=\"[a-z[A-Z][0-9][_]]*\""
    private val TEMPLATE_USER_TAKE_A_BITE = "meta name=\"user-login\" content=\""

    suspend fun auth(login: String, password: String, iHttpClient : IHttpClient) =
            parseUser(iHttpClient.post(
                EndpointGit.BASE.value,
                createHeadersForAuthorizate(),
                createFormDataForAuthorize(login, password, iHttpClient),
                arrayOf("session")))

    private suspend fun createFormDataForAuthorize(login: String, password: String, iHttpClient : IHttpClient) =
            hashMapOf<String, String>().apply {
                val resultRequest = iHttpClient.get(EndpointGit.BASE.value, mapOf(), mapOf(), arrayOf("login", "oauth", "authorize"))
                put(COMMIT_FORM_DATA, COMMIT_VALUE)
                put(TOKEN_FORM_DATA, parseToken(resultRequest))
                put(LOGIN_FORM_DATA, login)
                put(PASSWORD_FORM_DATA, password)
                put(TRUSTED_DEVICE, "")
                put(WEBAUTHN_SUPPORTED, WEBAUTHN_SUPPORTED_VALUE)
                put(WEBAUTHN_IUVPAA_SUPPORT, WEBAUTHN_IUVPAA_SUPPORT_VALUE)
                put(RETURN_TO, RETURN_TO_VALUE)
                put(ALLOW_SIGNUP, "")
                put(CLIENT_ID, "")
                put(INTEGRATION, "")
                put(REQUIRED_FIELD_5F9D, "")
                put(TIMESTAMP, parseTimestamp(resultRequest))
                put(TIMESTAMP_SECRET, parseTimestampSecret(resultRequest))
            }

    private fun createHeadersForAuthorizate() =
            hashMapOf<String, String>().apply {
                put(CONTENT_TYPE, CONTENT_TYPE_VALUE)
                put(REFERED, REFERED_VALUE)
            }

    private fun parseToken(string: String) = templateEngine(string, TEMPLATE_TOKEN, arrayOf(TEMPLATE_TOKEN_TAKE_A_BITE, CLOSABLE_TAKE_A_BITE)).get(0)

    private fun parseTimestamp(string: String) = templateEngine(string, TEMPLATE_TIMESTAMP, arrayOf(TEMPLATE_TIMESTAMP_TAKE_A_BITE, CLOSABLE_TAKE_A_BITE)).get(0)

    private fun parseTimestampSecret(string: String) = templateEngine(string, TEMPLATE_TIMESTAMP_SECRET, arrayOf(TEMPLATE_TIMESTAMP_SECRET_TAKE_A_BITE, CLOSABLE_TAKE_A_BITE)).get(0)

    private fun templateEngine(text: String, template: String, take_a_bite : Array<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        val pattern: Pattern = Pattern.compile(template)
        val matcher: Matcher = pattern.matcher(text)
        while (matcher.find()) {
            var res: String = matcher.group()

            take_a_bite.forEach {
                res = res.replace(it, "")
            }

            result.add(res)
        }
        return result
    }

    private fun parseUser(string : String) : String {
        //todo
        //else
        // <meta name="user-login" content="ramilka47">
        val user = templateEngine(string, TEMPLATE_USER, arrayOf(TEMPLATE_USER_TAKE_A_BITE, CLOSABLE_TAKE_A_BITE))
        if (user.isNullOrEmpty()) {
            throw AuthorizeException()
        } else {
            return user.get(0)
        }
    }
}