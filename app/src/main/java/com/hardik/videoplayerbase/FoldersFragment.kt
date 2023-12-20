package com.hardik.videoplayerbase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hardik.videoplayerbase.databinding.FragmentFoldersBinding

class FoldersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_folders, container, false)
        val binding = FragmentFoldersBinding.bind(view)
        val tempList = ArrayList<String>()
        tempList.add("First Folder")
        tempList.add("Second Folder")
        tempList.add("Third Folder")
        tempList.add("Fourth Folder")
        tempList.add("Fifth Folder")
        tempList.add("Sixth Folder")
        tempList.add("Seventh Folder")
        tempList.add("Eighth Folder")
        tempList.add("Ninth Folder")
        binding.FoldersRV.setHasFixedSize(true)
        binding.FoldersRV.setItemViewCacheSize(10)
        binding.FoldersRV.layoutManager = LinearLayoutManager(requireContext())
        try {
            binding.FoldersRV.adapter = FoldersAdapter(requireContext(), tempList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

}