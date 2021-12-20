package utils;

public class SortAlgorithms {

	// swaps elements i and j in array arr
	private static void swap(Sortable[] arr, int i, int j) {
		Sortable aux = arr[i];
		arr[i] = arr[j];
		arr[j] = aux;
	}

	public static void selectionSort(Sortable[] arr, boolean ascending) {

		for (int i = 0; i < arr.length; i++) {
			// find max /min
			int maxMin = i;
			for (int j = i + 1; j < arr.length; j++) {
				if (ascending && arr[j].sortValue() < arr[maxMin].sortValue()) {
					// sorting ascending and found smaller element
					maxMin = j;
				} else if (!ascending && arr[j].sortValue() > arr[maxMin].sortValue()) {
					// sorting descending and found greater element
					maxMin = j;
				}
			}
			swap(arr, i, maxMin);
		}
	}
}
