package dev.panwar.neuroinsight.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.panwar.neuroinsight.R
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dev.panwar.neuroinsight.models.request.QuestionResponses
import dev.panwar.neuroinsight.models.response.GetGADQuestionsResponseItem

class SurveyAdapter(
    var listQuestions: List<GetGADQuestionsResponseItem>,
    var responses: MutableMap<String,String> // Ensure it's mutable
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
        holder.chipOption1.text = item.options[0]
        holder.chipOption2.text = item.options[1]
        holder.chipOption3.text = item.options[2]
        holder.chipOption4.text = item.options[3]

        // Remove the previous listener by setting it to null
        holder.chipGroup.setOnCheckedChangeListener(null)

        // Clear all chip selections first
        holder.chipGroup.clearCheck()

        // Check if this question already has a response and restore it
        val existingResponse = responses[item.question.trim()]
        if (existingResponse != null) {
            when (existingResponse) {
                holder.chipOption1.text.toString() -> holder.chipOption1.isChecked = true
                holder.chipOption2.text.toString() -> holder.chipOption2.isChecked = true
                holder.chipOption3.text.toString() -> holder.chipOption3.isChecked = true
                holder.chipOption4.text.toString() -> holder.chipOption4.isChecked = true
            }
        }

        // Set chip selection listener
        holder.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val selectedResponse = selectedChip.text.toString().lowercase()
                responses[item.question.trim()] = selectedResponse
            } else {
                responses.remove(item.question.trim())
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestionHindi: TextView = itemView.findViewById(R.id.tv_question_hindi)
        val tvQuestionEnglish: TextView = itemView.findViewById(R.id.tv_question_english)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.chipGroup)
        val chipOption1: Chip = itemView.findViewById(R.id.chip_option13)
        val chipOption2: Chip = itemView.findViewById(R.id.chip_option2)
        val chipOption3: Chip = itemView.findViewById(R.id.chip_option3)
        val chipOption4: Chip = itemView.findViewById(R.id.chip_option4)
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
