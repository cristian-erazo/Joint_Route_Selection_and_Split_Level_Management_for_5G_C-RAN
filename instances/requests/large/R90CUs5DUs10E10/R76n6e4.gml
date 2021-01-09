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
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 7
    prb 5
    x 120
    y 40
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 7
    prb 6
    x 94
    y 98
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 4
    prb 3
    x 17
    y 60
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 7
    prb 4
    x 62
    y 59
  ]
  edge [
    source 0
    target 4
    bandwith 931
    delay 240
  ]
  edge [
    source 0
    target 3
    bandwith 120
    delay 109
  ]
  edge [
    source 1
    target 5
    bandwith 280
    delay 276
  ]
  edge [
    source 1
    target 2
    bandwith 976
    delay 149
  ]
]
