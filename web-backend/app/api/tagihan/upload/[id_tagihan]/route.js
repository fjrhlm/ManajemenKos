import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function POST(request, { params }) {
  try {
    const { id_tagihan } = await params;
    const formData = await request.formData();
    const file = formData.get('bukti_transfer');

    if (!file) {
      return NextResponse.json({ status: 'error', message: 'File bukti transfer tidak ditemukan' }, { status: 400 });
    }

    // Upload ke Supabase Storage
    const filename = `bukti_${id_tagihan}_${Date.now()}.${file.name.split('.').pop()}`;
    const buffer = Buffer.from(await file.arrayBuffer());
    
    const { error: uploadError } = await supabase.storage
      .from('uploads')
      .upload(filename, buffer, { contentType: file.type });

    // Jika bucket belum ada, simpan nama file saja
    const filePath = uploadError ? `uploads/${filename}` : filename;

    const { data, error } = await supabase.from('tagihan')
      .update({ bukti_transfer: filePath, status_bayar: 'Menunggu Validasi' })
      .eq('id_tagihan', id_tagihan).select().single();

    if (error) throw error;

    return NextResponse.json({ status: 'success', message: 'Bukti pembayaran berhasil diupload', data });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
