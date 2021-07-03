package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.dao.getConfig
import com.sunnyweather.android.logic.dao.showToast
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.FavViewModel
import com.sunnyweather.android.ui.weather.IndexOutBoundFragment
import com.sunnyweather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*

private const val ARG_INDEX = "index"

class PlaceFragment : Fragment() {
    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
    val fabViewModel by lazy { ViewModelProvider(this)[FavViewModel::class.java] }
    private lateinit var adapter: PlaceAdapter
    var index: Int = -1
    val favList = ArrayList<Place>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt(ARG_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (index == -1) {
            index = getConfig("index")
        }
        if (activity is MainActivity && viewModel.isPlaceSaved(index)) {
//            val place = viewModel.getSavedPlace(index)
            val intent = Intent(context, WeatherActivity::class.java).apply {
//                putExtra("location_lng", place.location.lng)
//                putExtra("location_lat", place.location.lat)
//                putExtra("place_name", place.name)
                putExtra("index", index)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        // 收藏夹加载
        getFavList()
        showToast("favList empty:${favList.isEmpty()}")
        if (favList.isNotEmpty()) {
            viewModel.placeList.clear()
            viewModel.placeList.addAll(favList)
            recyclerView.visibility = View.VISIBLE
            bgImageView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            bgImageView.visibility = View.VISIBLE
        }

        // 事件监听
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        searchPlaceEdit.addTextChangedListener { text: Editable? ->
            val content = text.toString()
            if (content.isNotBlank()) {
                viewModel.searchPlaces(content)
            } else {
                getFavList()
                if (favList.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    bgImageView.visibility = View.VISIBLE
                    viewModel.placeList.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    viewModel.placeList.clear()
                    viewModel.placeList.addAll(favList)
                    adapter.notifyDataSetChanged()
                }
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
    fun getFavList(){
        favList.clear()
        favList.addAll(fabViewModel.getFavList())
    }
    override fun onResume() {
        super.onResume()
        showToast("on Resume")
        getFavList()
        if (searchPlaceEdit.text.toString().isBlank()) {
            if (favList.isEmpty()) {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            } else {
                viewModel.placeList.clear()
                viewModel.placeList.addAll(favList)
                adapter.notifyDataSetChanged()
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IndexOutBoundFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(index: Int) =
            PlaceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_INDEX, index)
                }
            }
    }
}