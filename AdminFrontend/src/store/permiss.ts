import { defineStore } from 'pinia';

interface ObjectList {
	[key: string]: string[];
}

export const usePermissStore = defineStore('permiss', {
	state: () => {
		const keys = localStorage.getItem('ms_keys');
		return {
			key: keys ? JSON.parse(keys) : <string[]>[],
			defaultList: <ObjectList>{
				admin: ['1'],
				canteen_admin: ['1']
			}
		};
	},
	actions: {
		handleSet(val: string[]) {
			this.key = val;
			localStorage.setItem('ms_keys', JSON.stringify(val));
		},
		handleRemove() {
			localStorage.removeItem('ms_keys');
		}
	}
});
