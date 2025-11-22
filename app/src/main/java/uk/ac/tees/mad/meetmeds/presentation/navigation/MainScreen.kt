package uk.ac.tees.mad.meetmeds.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist.MedicineListScreen
import uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist.MedicineListViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MedicineListViewModel = hiltViewModel()
) {
    MedicineListScreen(navController)
}