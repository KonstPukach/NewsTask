package com.pukachkosnt.newstask.ui.listnews.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import com.pukachkosnt.newstask.R
import com.pukachkosnt.newstask.databinding.FragmentListNewsBinding
import com.pukachkosnt.newstask.ui.listnews.BaseListNewsFragment
import com.pukachkosnt.newstask.ui.listnews.all.ListNewsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseListNewsFragment() {
    override val viewModel: FavoritesViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityNews = (context as AppCompatActivity)
        activityNews.supportActionBar?.setDisplayShowHomeEnabled(true)
        activityNews.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_news, container, false)
        binding = FragmentListNewsBinding.bind(view)
        binding.textViewNothingFound.isVisible = false
        binding.swipeRefreshListNews.isEnabled = false

        setupRecyclerView()

        return binding.root
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        viewModel.newsItemsLiveData.observe(    // submit data to an adapter
            viewLifecycleOwner,
            {
                it?.let {
                    newsAdapter.submitData(lifecycle, it.data)
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        setFragmentResult(
            ListNewsFragment.F_RESULT_DELETED_ITEMS,
            Bundle().apply {
                putStringArrayList(
                    ListNewsFragment.KEY_DELETED_ITEMS,
                    ArrayList(viewModel.deletedItems)
                )
            }
        )
    }
}