package com.yonder.weightly.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentOnBoardingBinding
import com.yonder.weightly.uicomponents.CardRuler
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    private val binding by viewBinding(FragmentOnBoardingBinding::bind)

    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    OnBoardingViewModel.Event.NavigateToHome -> {
                        findNavController().navigate(OnBoardingFragmentDirections.actionNavigateHome())
                    }
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        cardRulerCurrent.render(CardRuler(R.string.current_weight, R.string.enter_current_weight))
        cardRulerGoal.render(CardRuler(R.string.goal_weight, R.string.enter_goal_weight))
        btnContinue.setOnClickListener {
            val currentWeight: Float = cardRulerCurrent.value
            val goalWeight: Float = cardRulerGoal.value
            viewModel.save(currentWeight = currentWeight, goalWeight = goalWeight)
        }
    }

}