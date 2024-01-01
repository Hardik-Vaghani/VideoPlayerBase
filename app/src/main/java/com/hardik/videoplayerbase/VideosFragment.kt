package com.hardik.videoplayerbase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.hardik.videoplayerbase.databinding.FragmentVideosBinding

class VideosFragment : Fragment() {

    private lateinit var binding: FragmentVideosBinding
    lateinit var adapter: VideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.themesList[MainActivity.themeIndex], true)//set theme inside of fragment
        val view = inflater.inflate(R.layout.fragment_videos, container, false)
        binding = FragmentVideosBinding.bind(view)
        binding.videoRV.setHasFixedSize(true)
        binding.videoRV.setItemViewCacheSize(10)
        binding.videoRV.layoutManager = LinearLayoutManager(requireContext())
        try {
            // initialize adapter
            adapter = VideoAdapter(requireContext(), MainActivity.videoList)
            binding.videoRV.adapter = adapter
            binding.totalVideos.text = "Total Videos: ${MainActivity.videoList.size}"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //for refreshing layout
        binding.root.setOnRefreshListener {
            MainActivity.videoList = getAllVideos(requireContext())
            adapter.updateList(MainActivity.videoList)
            binding.totalVideos.text = "Total Videos: ${MainActivity.videoList.size}"

            binding.root.isRefreshing = false
        }

        binding.nowPlayingBtn.setOnClickListener{
            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("class","NowPlaying")//intent passing
            startActivity(intent)
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
                    //every time initialize searchList
                    MainActivity.searchList = ArrayList()
                    for (video in MainActivity.videoList){
                        if (video.title.lowercase().contains(newText.lowercase())){
                            MainActivity.searchList.add(video)
                        }
                    }
                    MainActivity.search = true
                    adapter.updateList(searchList = MainActivity.searchList)
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (PlayerActivity.position != -1)binding.nowPlayingBtn.visibility = View.VISIBLE
        if (MainActivity.adapterChanged) adapter.notifyDataSetChanged() //when adapter changed is rename use that time execution
        MainActivity.adapterChanged = false //when adapter changed is rename use that time execution
    }
}