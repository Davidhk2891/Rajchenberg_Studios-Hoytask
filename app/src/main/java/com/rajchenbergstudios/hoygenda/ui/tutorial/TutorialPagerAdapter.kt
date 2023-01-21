package com.rajchenbergstudios.hoygenda.ui.tutorial

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    //Array ?
    private val tutorialPagerTitles = ArrayOf()

    override fun getCount(): Int {

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.layout_tutorial_pager, container, false)

        val tutorialPagerTitle: View = view.findViewById<TextView>(R.id.tutorial_pager_title)
        val tutorialPagerImage: View = view.findViewById<TextView>(R.id.tutorial_pager_image)
        val tutorialPagerContent: View = view.findViewById<TextView>(R.id.tutorial_pager_content)

        container.addView(view)

        return view
    }
}