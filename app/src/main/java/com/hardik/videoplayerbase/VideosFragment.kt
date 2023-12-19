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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_videos, container, false)
        val binding = FragmentVideosBinding.bind(view)
        val tempList = ArrayList<String>()
        tempList.add("First Video")
        tempList.add("Second Video")
        tempList.add("Third Video")
        tempList.add("Fourth Video")
        tempList.add("Fifth Video")
        tempList.add("Sixth Video")
        tempList.add("Seventh Video")
        tempList.add("Eighth Video")
        tempList.add("Ninth Video")
        tempList.add("Tenth Video")
        binding.VideoRV.setHasFixedSize(true)
        binding.VideoRV.setItemViewCacheSize(10)
        binding.VideoRV.layoutManager = LinearLayoutManager(requireContext())
        binding.VideoRV.adapter = VideoAdapter(requireContext(),tempList)
        return view
    }

}