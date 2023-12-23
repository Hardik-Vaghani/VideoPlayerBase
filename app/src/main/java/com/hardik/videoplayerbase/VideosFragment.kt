package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.hardik.videoplayerbase.databinding.FragmentVideosBinding

class VideosFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_videos, container, false)
        val binding = FragmentVideosBinding.bind(view)
        binding.videoRV.setHasFixedSize(true)
        binding.videoRV.setItemViewCacheSize(10)
        binding.videoRV.layoutManager = LinearLayoutManager(requireContext())
        try {
            binding.videoRV.adapter = VideoAdapter(requireContext(), MainActivity.videoList)
            binding.totalVideos.text = "Total Videos: ${MainActivity.videoList.size}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view,menu)
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null){
                    Toast.makeText(requireContext(),"new Text$newText",Toast.LENGTH_SHORT).show()
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

}