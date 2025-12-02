import api from "./api";

export const pdfService = {
  getPdfPreview: async (id: number): Promise<Uint8Array> => {
    const response = await api.get(`/exports/${id}`, {
      responseType: "arraybuffer",
    });
    return new Uint8Array(response.data);
  },

  getPdfDownload: async (id: number) => {
    const response = await api.get(`/exports/${id}/download`, {
      responseType: "blob",
    });

    const url = window.URL.createObjectURL(response.data);
    const a = document.createElement("a");
    a.href = url;
    a.download = "crm_report.pdf";
    a.click();
    window.URL.revokeObjectURL(url);
  },
};
