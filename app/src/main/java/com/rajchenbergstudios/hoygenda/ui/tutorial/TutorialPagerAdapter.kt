package com.rajchenbergstudios.hoygenda.ui.tutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.rajchenbergstudios.hoygenda.R


class TutorialPagerAdapter (private val context: Context) : PagerAdapter() {

    private val tutorialPagerImages = intArrayOf(
        R.drawable.ic_baseline_tag_faces_24,
        R.drawable.ic_baseline_add_task_24,
        R.drawable.ic_baseline_checklist_24,
        R.drawable.ic_baseline_history_24,
        R.drawable.ic_baseline_question_answer_24
    )

    private val tutorialPagerTitles = arrayOf(
        R.string.slide_one_title,
        R.string.slide_two_title,
        R.string.slide_three_title,
        R.string.slide_four_title,
        R.string.slide_five_title
    )

    private val tutorialPagerContent = arrayOf(
        R.string.slide_one_content,
        R.string.slide_two_content,
        R.string.slide_three_content,
        R.string.slide_four_content,
        R.string.slide_five_content
    )

    override fun getCount(): Int {
        return tutorialPagerImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.layout_tutorial_pager, container, false)

        val tutorialPagerTitle: TextView = view.findViewById(R.id.tutorial_pager_title)
        val tutorialPagerImage: ImageView = view.findViewById(R.id.tutorial_pager_image)
        val tutorialPagerContent: TextView = view.findViewById(R.id.tutorial_pager_content)

        tutorialPagerTitle.setText(tutorialPagerTitles[position])
        tutorialPagerImage.setImageResource(tutorialPagerImages[position])
        tutorialPagerContent.setText(this.tutorialPagerContent[position])

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}