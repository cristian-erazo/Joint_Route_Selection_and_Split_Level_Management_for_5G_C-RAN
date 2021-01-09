graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 1
    prc 4
    ant 4
    prb 3
    x 42
    y 18
  ]
  node [
    id 2
    label "2"
    type 1
    prc 4
    ant 2
    prb 4
    x 81
    y 41
  ]
  edge [
    source 0
    target 1
    bandwith 253
    delay 195
  ]
  edge [
    source 0
    target 2
    bandwith 775
    delay 360
  ]
]
