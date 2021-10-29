package keehl.tagapi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeSet<K, E> {

	private final Map<K, List<E>> values = new HashMap<>();

	public void put(K key, List<E> value) {
		this.values.put(key, value);
	}

	public void add(K key, E value) {
		if (!this.values.containsKey(key))
			this.values.put(key, new ArrayList<>());
		if (value == null)
			return;
		this.values.get(key).add(value);
	}

	@SuppressWarnings("unchecked")
	public void add(K key, E... values) {
		if (!this.values.containsKey(key))
			this.values.put(key, new ArrayList<>());
		for (E value : values)
			this.add(key, value);
	}

	public void remove(K key, E value) {
		if (!this.values.containsKey(key))
			return;
		if (!this.values.get(key).contains(value))
			return;
		this.values.get(key).remove(value);
	}

	public void clear(K key) {
		this.values.remove(key);
	}

	public void clear() {
		this.values.clear();
	}

	public void addAll(K key, List<E> values) {
		if (!this.values.containsKey(key))
			this.values.put(key, new ArrayList<>());
		this.values.get(key).addAll(values);
	}

	public void addBlank(K key){
		if (!this.values.containsKey(key))
			this.values.put(key, new ArrayList<>());
	}

	public void removeAll(K key, List<E> values) {
		if (!this.values.containsKey(key))
			return;
		this.values.get(key).removeAll(values);
	}

	public List<E> get(K key) {
		if (!this.values.containsKey(key))
			return new ArrayList<>();
		return this.values.get(key);
	}

	public List<E> getKey(K key) {
		if (!this.values.containsKey(key))
			return new ArrayList<>();
		return this.values.get(key);
	}

	public List<E> get(int index) {
		if (this.values.isEmpty())
			return new ArrayList<>();
		return this.values.get(new ArrayList<>(this.values.keySet()).get(index));
	}

	public List<E> getIndex(int index) {
		if (this.values.isEmpty())
			return new ArrayList<>();
		return this.values.get(new ArrayList<>(this.values.keySet()).get(index));
	}

	public int size() {
		return this.values.size();
	}

	public boolean containsKey(K key) {
		return this.values.containsKey(key);
	}

	public List<K> keySet() {
		if (this.values.isEmpty())
			return new ArrayList<>();
		return new ArrayList<>(this.values.keySet());
	}

	public List<E> values() {
		if (this.values.isEmpty())
			return new ArrayList<>();
		List<E> values = new ArrayList<>();
		for (K key : this.values.keySet())
			values.addAll(this.values.get(key));
		return values;
	}

}