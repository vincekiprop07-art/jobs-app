package com.example.jobsapp.ui.cv

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.jobsapp.databinding.FragmentCvModifierBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CvModifierFragment : Fragment() {

    private var _binding: FragmentCvModifierBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy {
        requireContext().getSharedPreferences("jobsapp_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCvModifierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApiKey.setOnClickListener { showApiKeyDialog() }

        binding.btnRewrite.setOnClickListener {
            val cvText = binding.etCvInput.text.toString().trim()
            val apiKey = prefs.getString("anthropic_api_key", null)

            if (apiKey.isNullOrBlank()) {
                showApiKeyDialog()
                return@setOnClickListener
            }
            if (cvText.isBlank()) {
                binding.tvOutput.text = "Paste your CV text first."
                return@setOnClickListener
            }

            setLoading(true)

            lifecycleScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        CvRewriteClient.rewrite(apiKey, cvText)
                    }
                    binding.tvOutput.text = result.rewritten
                    if (result.changes.isNotEmpty()) {
                        binding.tvChangelog.text =
                            result.changes.joinToString("\n") { "• $it" }
                        binding.changelogContainer.visibility = View.VISIBLE
                    } else {
                        binding.changelogContainer.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    binding.tvOutput.text = "Something went wrong: ${e.message ?: "unknown error"}"
                } finally {
                    setLoading(false)
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRewrite.isEnabled = !loading
        binding.btnRewrite.text = if (loading) "Rewriting…" else "Rewrite for global readability"
        if (loading) {
            binding.tvOutput.text = "Working on it…"
            binding.changelogContainer.visibility = View.GONE
        }
    }

    private fun showApiKeyDialog() {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        input.hint = "sk-ant-..."
        prefs.getString("anthropic_api_key", null)?.let { input.setText(it) }

        AlertDialog.Builder(requireContext())
            .setTitle("Anthropic API key")
            .setMessage(
                "Needed to power the CV rewriter. Stored only on this device. " +
                    "Get a key from console.anthropic.com - see the README for production setup notes."
            )
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                prefs.edit().putString("anthropic_api_key", input.text.toString().trim()).apply()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
