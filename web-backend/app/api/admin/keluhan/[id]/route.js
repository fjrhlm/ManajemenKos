import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

// Hapus keluhan
export async function DELETE(request, { params }) {
  try {
    const { id } = await params;
    await supabase.from('keluhan').delete().eq('id_keluhan', id);
    return NextResponse.json({ status: 'success', message: 'Keluhan berhasil dihapus' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
