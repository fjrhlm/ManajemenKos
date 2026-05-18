import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET() {
  try {
    const { data: keluhan } = await supabase.from('keluhan').select('*').order('id_keluhan', { ascending: false });
    const result = [];
    for (const k of keluhan || []) {
      const { data: user } = await supabase.from('users').select('nama').eq('id', k.id_user).single();
      result.push({ ...k, nama: user?.nama || 'Unknown' });
    }
    return NextResponse.json({ data: result });
  } catch (e) {
    return NextResponse.json({ data: [] });
  }
}
