package com.rajchenbergstudios.hoygenda.ui.tutorial

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.rajchenbergstudios.hoygenda.R
import com.rajchenbergstudios.hoygenda.databinding.FragmentTutorialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialFragment : Fragment(R.layout.fragment_tutorial) {

    private val viewModel: TutorialViewModel by viewModels()

    private lateinit var viewPager: ViewPager
    private lateinit var dotsLinearLayout: LinearLayout

    private lateinit var tutorialSlidePrevious: ImageView
    private lateinit var tutorialSlideNext: ImageView

    private var currentPage: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        val binding = FragmentTutorialBinding.bind(view)

        addViews(binding)
        addDotsIndicator(0)
        elementEnabled(tutorialSlidePrevious, false, View.INVISIBLE)
        addViewPagerListener()
        buttonsListeners()
        loadTutorialEventCollector()
    }

    private fun addViews(binding: FragmentTutorialBinding) {
        viewPager = binding.tutorialViewpager
        val adapter = context?.let { TutorialPagerAdapter(it) }
        viewPager.adapter = adapter
        tutorialSlidePrevious = binding.tutorialImageviewPrevious
        tutorialSlideNext = binding.tutorialImageviewForward
        dotsLinearLayout = binding.tutorialDotsLinearlayout
    }

    private fun addViewPagerListener() {
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                addDotsIndicator(position)
                currentPage = position
                sliderButtonsState()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun addDotsIndicator(position: Int) {
        val dots = arrayOfNulls<TextView>(5)
        dotsLinearLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(activity)
            dots[i]?.text = Html.fromHtml("&#8226;", 0)
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(Color.parseColor("#F5F5F5"))
            dotsLinearLayout.addView(dots[i])
        }
        dots[position]?.setTextColor(Color.parseColor("#646464"))
    }

    private fun sliderButtonsState() {
        when (currentPage) {
            0 -> {
                elementEnabled(tutorialSlidePrevious, false, View.INVISIBLE)
                elementEnabled(tutorialSlideNext, true, View.VISIBLE)
            }
            1 -> {
                elementEnabled(tutorialSlidePrevious, true, View.VISIBLE)
                elementEnabled(tutorialSlideNext, true, View.VISIBLE)
            }
            2 -> {
                elementEnabled(tutorialSlidePrevious, true, View.VISIBLE)
                elementEnabled(tutorialSlideNext, true, View.VISIBLE)
            }
            3 -> {
                elementEnabled(tutorialSlidePrevious, true, View.VISIBLE)
                elementEnabled(tutorialSlideNext, true, View.VISIBLE)
                setForwardArrow()
            }
            4 -> {
                elementEnabled(tutorialSlidePrevious, true, View.VISIBLE)
                setCheckmarkToFinish()
            }
        }
    }

    private fun buttonsListeners() {
        tutorialSlidePrevious.setOnClickListener {
            viewPager.currentItem = currentPage - 1
        }
        tutorialSlideNext.setOnClickListener {
            if (currentPage == 4) {
                // Works. The reason has something to do with calling the flow inside a collector
                // What I was doing was calling dataStore.edit (which is a suspend function) inside a coroutine (this.lifecycleScope.launchWhenStarted)
                // Don't do this. The save won't run
                viewModel.onSaveTutorialAutoRunDone()
                viewModel.onNavigateToTodayFragmentEngaged()
            } else {
                viewPager.currentItem = currentPage + 1
            }
        }
    }
    private fun loadTutorialEventCollector() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tutorialEvent.collect { event ->
                when (event) {
                    TutorialViewModel.TutorialEvent.NavigateToTodayFragment -> {
                        val action = TutorialFragmentDirections.actionGlobalTodayFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun setCheckmarkToFinish() {
        tutorialSlideNext.setImageResource(R.drawable.ic_baseline_check_24)
    }

    private fun setForwardArrow() {
        tutorialSlideNext.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24)
    }

    private fun elementEnabled(view: View, enabled: Boolean, visibility: Int) {
        view.isEnabled = enabled
        view.isClickable = enabled
        view.visibility = visibility
    }
}