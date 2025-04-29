package dev.panwar.neuroinsight.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import dev.panwar.neuroinsight.R
import dev.panwar.neuroinsight.models.response.GetGADQuestionsResponseItem

class SurveyAdapter(
    private val context: Context,
    var listQuestions: List<GetGADQuestionsResponseItem>,
    var responses: MutableMap<String, String>
) : RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_survey_question, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listQuestions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listQuestions[position]
        val questions = separateLanguages(item.question)
        holder.tvQuestionHindi.text = "${position + 1}. ${questions?.first}"
        holder.tvQuestionEnglish.text = questions?.second
        holder.radioOption1.text = item.optionsList[0]
        holder.radioOption2.text = item.optionsList[1]
        holder.radioOption3.text = item.optionsList[2]
        holder.radioOption4.text = item.optionsList[3]

        // Clear all radio button selections first
        holder.radioOption1.isChecked = false
        holder.radioOption2.isChecked = false
        holder.radioOption3.isChecked = false
        holder.radioOption4.isChecked = false

        // Reset card backgrounds
        resetCardBackgrounds(holder)

        // Check if this question already has a response and restore it
        val existingResponse = responses[item.question.trim()]
        if (existingResponse != null) {
            when (existingResponse.lowercase()) {
                holder.radioOption1.text.toString().lowercase() -> {
                    holder.radioOption1.isChecked = true
                    updateCardBackground(holder.cardOption1, true)
                }
                holder.radioOption2.text.toString().lowercase() -> {
                    holder.radioOption2.isChecked = true
                    updateCardBackground(holder.cardOption2, true)
                }
                holder.radioOption3.text.toString().lowercase() -> {
                    holder.radioOption3.isChecked = true
                    updateCardBackground(holder.cardOption3, true)
                }
                holder.radioOption4.text.toString().lowercase() -> {
                    holder.radioOption4.isChecked = true
                    updateCardBackground(holder.cardOption4, true)
                }
            }
        }

        setupRadioButtonClickListeners(holder, item)
    }

    private fun setupRadioButtonClickListeners(holder: ViewHolder, item: GetGADQuestionsResponseItem) {
        val radioOptions = listOf(
            holder.radioOption1,
            holder.radioOption2,
            holder.radioOption3,
            holder.radioOption4
        )

        val cardOptions = listOf(
            holder.cardOption1,
            holder.cardOption2,
            holder.cardOption3,
            holder.cardOption4
        )

        radioOptions.forEachIndexed { index, radioButton ->
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Update card appearance
                    updateCardBackground(cardOptions[index], true)

                    // Save the response
                    responses[item.question.trim()] = radioButton.text.toString().lowercase()

                    // Deselect other options
                    radioOptions.forEachIndexed { otherIndex, otherButton ->
                        if (otherIndex != index && otherButton.isChecked) {
                            otherButton.isChecked = false
                            updateCardBackground(cardOptions[otherIndex], false)
                        }
                    }
                }
            }
        }
    }

    private fun updateCardBackground(cardView: MaterialCardView, isSelected: Boolean) {
        if (isSelected) {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            cardView.strokeColor = ContextCompat.getColor(context, R.color.secondary_text_color)
        } else {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.white)
            )
            cardView.strokeColor = ContextCompat.getColor(context, R.color.secondary_text_color)
        }
    }

    private fun resetCardBackgrounds(holder: ViewHolder) {
        val cardOptions = listOf(
            holder.cardOption1,
            holder.cardOption2,
            holder.cardOption3,
            holder.cardOption4
        )

        cardOptions.forEach { cardView ->
            updateCardBackground(cardView, false)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestionHindi: TextView = itemView.findViewById(R.id.tv_question_hindi)
        val tvQuestionEnglish: TextView = itemView.findViewById(R.id.tv_question_english)

        val cardOption1: MaterialCardView = itemView.findViewById(R.id.card_option1)
        val cardOption2: MaterialCardView = itemView.findViewById(R.id.card_option2)
        val cardOption3: MaterialCardView = itemView.findViewById(R.id.card_option3)
        val cardOption4: MaterialCardView = itemView.findViewById(R.id.card_option4)

        val radioOption1: RadioButton = itemView.findViewById(R.id.radio_option1)
        val radioOption2: RadioButton = itemView.findViewById(R.id.radio_option2)
        val radioOption3: RadioButton = itemView.findViewById(R.id.radio_option3)
        val radioOption4: RadioButton = itemView.findViewById(R.id.radio_option4)
    }

    fun separateLanguages(input: String): Pair<String, String>? {
        val parts = input.split("/")
        return if (parts.size == 2) {
            Pair(parts[0].trim(), parts[1].trim()) // Hindi -> parts[0], English -> parts[1]
        } else {
            null // Return null if the format is incorrect
        }
    }
}