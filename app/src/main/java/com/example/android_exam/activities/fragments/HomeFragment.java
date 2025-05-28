package com.example.android_exam.activities.fragments;

import com.example.android_exam.viewmodel.FoodViewModel;

public class HomeFragment extends Fragment {

    private FoodViewModel viewModel;
    private IngredientAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IngredientAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(FoodViewModel.class);

        viewModel.getAllIngredients().observe(getViewLifecycleOwner(), ingredients -> {
            adapter.setIngredients(ingredients);
            // Kiểm tra cảnh báo
            boolean hasExpiring = false;
            Date now = new Date();
            for (IngredientEntity item : ingredients) {
                if (item.getExpiryDate() != null &&
                        item.getExpiryDate().before(new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000))) {
                    hasExpiring = true;
                    break;
                }
            }
            view.findViewById(R.id.text_warning).setVisibility(hasExpiring ? View.VISIBLE : View.GONE);
        });

        view.findViewById(R.id.button_ai_suggest).setOnClickListener(v -> {
            // Gọi API gợi ý món ăn
            suggestMealFromAI();
        });
    }

    private void suggestMealFromAI() {
        // Gọi ra màn hình gợi ý món ăn hoặc dialog AI
    }
}
