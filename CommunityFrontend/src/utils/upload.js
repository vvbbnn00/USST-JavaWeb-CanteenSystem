import {fetchApiWithAuth} from "@/utils/api";

export const selectFile = (maxFileCount) => {
    const SUPPORT_FILE_TYPES = ["image/png", "image/jpeg", "image/gif", "image/webp"];
    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.multiple = true;
    fileInput.accept = SUPPORT_FILE_TYPES.join(",");
    fileInput.click();
    return new Promise((resolve, reject) => {
        fileInput.onchange = (e) => {
            const files = e.target.files;
            if (files.length > maxFileCount) {
                reject("文件数量超过上限，最多只能上传" + maxFileCount + "个文件");
            }
            console.log(files);
            resolve(Array.from(files));
        }
    })
}


export const getUploadUrl = () => {
    return fetchApiWithAuth("/api/rest/image/upload", {
        method: "POST"
    });
}
