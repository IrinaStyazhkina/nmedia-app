package ru.netology.nmedia.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.OnInteractionListener
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentOnePostBinding
import ru.netology.nmedia.fragment.NewPostFragment.Companion.textArg
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewModel.PostViewModel

class OnePostFragment: Fragment() {

    companion object {
        var Bundle.postId: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOnePostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.postId?.let {
            val postId = it.toLong()
            val post = viewModel.getById(postId)

            if(post != null) {
                val viewHolder = PostViewHolder(binding.cardPostContainer, object :
                    OnInteractionListener {

                    override fun onLike(post: Post) {
                        viewModel.likeById(post.id)
                    }

                    override fun onShare(post: Post) {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type="text/plain"
                            putExtra(Intent.EXTRA_TEXT, post.content)
                        }

                        val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                        viewModel.shareById(post.id)
                    }

                    override fun onPlay(post: Post) {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(post.video)
                        }
                        val playIntent = Intent.createChooser(intent, getString(R.string.chooser_play_video))
                        startActivity(playIntent)
                    }

                    override fun onRemove(post: Post) {
                        viewModel.removeById(post.id)
                        findNavController().navigateUp()
                    }

                    override fun onEdit(post: Post) {
                        viewModel.edit(post)
                        findNavController()
                            .navigate(
                                R.id.action_onePostFragment_to_newPostFragment,
                                Bundle().apply {
                                    textArg = post.content
                                }
                            )
                    }
                })
                viewHolder.bind(post)

                viewModel.data.observe(viewLifecycleOwner) { posts ->
                    val updatedPost = posts.find { it.id == postId }
                    viewHolder.bind(updatedPost ?: post)
                }
            }
        }

        return binding.root
    }
}