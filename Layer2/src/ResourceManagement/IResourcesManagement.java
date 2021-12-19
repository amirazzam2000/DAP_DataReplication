package ResourceManagement;

public interface IResourcesManagement {
    int updateValue(int position, int value);
    int readValue(int position);
    int updateValuePrivately(int position, int value);
    void updateArray(int[] array);
}
