package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.GONE
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.fragment.AttachmentFragment.Companion.urlArg
import ru.netology.nmedia.fragment.NewPostFragment.Companion.textArg
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.viewModel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false,
        )
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                if (viewModel.authenticated) {
                    viewModel.likeById(post.id)
                } else {
                    SignInDialogFragment().show(
                        childFragmentManager, SignInDialogFragment.TAG)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController()
                    .navigate(
                        R.id.action_feedFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        }
                    )
            }

            override fun onClickPhoto(attachment: Attachment) {
                findNavController()
                    .navigate(
                        R.id.action_feedFragment_to_attachmentFragment,
                        Bundle().apply {
                            urlArg = attachment.url
                        }
                    )
            }
        })
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter{ adapter.retry() },
            footer = PostLoadingStateAdapter{ adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter::submitData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {state ->
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if(state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry_loading) {
                        adapter.refresh()
                    }
                    .show()
            }
        }

        viewModel.postDeleted.observe(viewLifecycleOwner) { data ->
            if (!data.isSuccessful) {
                Snackbar.make(binding.root, R.string.error_deleting, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.retry_loading) {
                        viewModel.removeById(data.id)
                    }
                    .show()
            }
        }

        viewModel.postLikeChanged.observe(viewLifecycleOwner) {
            if (it == false) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)

        binding.fab.setOnClickListener {
            if (viewModel.authenticated) {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                SignInDialogFragment().show(
                    childFragmentManager, SignInDialogFragment.TAG)
            }
        }

        adapter.registerAdapterDataObserver(object : AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if(positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

//        viewModel.newerCount.observe(viewLifecycleOwner) {
//            Log.d("Posts count", "New posts count: $it")
//            viewModel.getUnreadPostsCount()
//        }

        viewModel.postHiddenCountChanged.observe(viewLifecycleOwner) {
            if(it > 0) {
                binding.newPosts.visibility = VISIBLE
            } else {
                binding.newPosts.visibility = GONE
            }
        }

        binding.newPosts.setOnClickListener {
            viewModel.readAllPosts()
        }

        return binding.root
    }
}