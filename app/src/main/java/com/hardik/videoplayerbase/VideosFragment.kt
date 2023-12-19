package com.hardik.videoplayerbase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.hardik.videoplayerbase.databinding.FragmentVideosBinding

class VideosFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_videos, container, false)
        val binding = FragmentVideosBinding.bind(view)
        binding.VideoRV.setHasFixedSize(true)
        binding.VideoRV.setItemViewCacheSize(10)
        binding.VideoRV.layoutManager = LinearLayoutManager(requireContext())
        try {
            binding.VideoRV.adapter = VideoAdapter(requireContext(), MainActivity.videoList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

}