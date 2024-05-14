package audio.switchboard.voicemodlocalplayback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import audio.switchboard.voicemodlocalplayback.databinding.ExampleItemBinding
import audio.switchboard.voicemodlocalplayback.databinding.FragmentMainBinding
import audio.switchboard.voicemodlocalplayback.utils.ExampleProvider

class MainFragment : Fragment() {

    companion object {
        val TAG = this::class.java.name
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val examples = ExampleProvider.examples()
        val adapter = ExampleAdapter(examples.map { it.title }.toTypedArray()) {
            val example = examples[it]
            val fragment = example.fragment.newInstance()
            (requireActivity() as MainActivity).pushFragment(fragment)
        }
        binding.exampleList.adapter = adapter

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private class ExampleAdapter(
        private val dataSet: Array<String>,
        private val clickAction: (Int) -> Unit
    ) : RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

        class ViewHolder(binding: ExampleItemBinding) : RecyclerView.ViewHolder(binding.root) {
            val textView: TextView = binding.textView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(
                ExampleItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = dataSet[position]
            holder.itemView.setOnClickListener {
                clickAction(position)
            }
        }

        override fun getItemCount(): Int = dataSet.size
    }
}